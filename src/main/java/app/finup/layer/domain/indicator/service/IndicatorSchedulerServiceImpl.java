package app.finup.layer.domain.indicator.service;


import app.finup.api.external.financialindex.client.FinancialIndexClient;
import app.finup.api.external.marketindex.client.MarketIndexClient;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.TimeUtils;
import app.finup.layer.domain.indicator.dto.IndicatorDto;
import app.finup.layer.domain.indicator.dto.IndicatorDtoMapper;
import app.finup.layer.domain.indicator.redis.IndicatorRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * IndicatorSchedulerService 구현 클래스
 * @author kcw
 * @since 2026-01-14
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IndicatorSchedulerServiceImpl implements IndicatorSchedulerService {

    // 사용 의존성
    private final IndicatorRedisStorage indicatorRedisStorage;
    private final MarketIndexClient marketIndexClient;
    private final FinancialIndexClient financialIndexClient;

    // 사용 상수
    private static final int MAX_FALLBACK_DAYS = 15; // 만약 오늘이 영업일이 아닌 경우, 최대 15일 전까지 조회 시도

    @Override
    public void syncFinancialIndex() {

        // [1] 인덱스 정보 조회를 위해 현재 날짜 계산
        // 현재 한국시간 기준 11시 10분 이전의 요청이라면 어제 날짜. 이후라면 오늘 날짜로 지정
        LocalDate date = calculateTargetDate(LocalTime.of(11, 10));


        // [2] API 조회 및 조회결과 변환
        List<IndicatorDto.FinancialIndexRow> indexRows =
                getFinancialIndicator(date, financialIndexClient::getExchangeRates, IndicatorDtoMapper::toFinancialIndexRow);

        // [3] Redis 내 저장
        indicatorRedisStorage.storeFinancialIndexes(indexRows);
    }


    @Override
    public void syncMarketIndex() {

        // [1] 인덱스 정보 조회를 위해 현재 날짜 계산
        // 현재 한국시간 기준 13시 10분 이전의 요청이라면 어제 날짜. 이후라면 오늘 날짜로 지정
        LocalDate date = calculateTargetDate(LocalTime.of(13, 10));

        // [2] API 조회 및 조회결과 변환
        List<IndicatorDto.MarketIndexRow> indexRows =
                getMarketIndicator(date, marketIndexClient::getIndexList, IndicatorDtoMapper::toMarketIndexRows);

        // [3] Redis 내 저장
        indicatorRedisStorage.storeMarketIndexes(indexRows);
    }


    // 조회할 기준 날짜 계산
    private LocalDate calculateTargetDate(LocalTime cutoffTime) {

        // [1] 한국 시간 기준 시간 정의
        ZonedDateTime nowKst = ZonedDateTime.now(TimeUtils.ZONE_ID_KOREA);

        // [2] 한국 시간 기준 날짜 계산
        return nowKst.toLocalTime().isBefore(cutoffTime)
                ? nowKst.toLocalDate().minusDays(1) // 만약 기준 시간보다 이전이면 어제 날짜
                : nowKst.toLocalDate(); // 기준 시간 이후이면 오늘 날짜
    }


    // 경제지표 API 조회 시도 (FallBack 일수까지)
    // C : Client 요청 결과 DTO 클래스, R : 반환 클래스
    private <C, R> List<R> getFinancialIndicator(
            LocalDate startDate,
            Function<LocalDate, List<C>> callClientMethod,
            BiFunction<List<C>, List<C>, List<R>> mappingMethod) {

        // [1] 두 결과 날을 저장할 목록
        List<List<C>> rp = new ArrayList<>();

        // [2] 가장 최근 영업일의 지표 조회
        Pair<List<C>, Integer> firstResult =
                getIndicator(fallbackDay -> callClientMethod.apply(startDate.minusDays(fallbackDay)));

        // [3] 최근 영업일과 등락률 계산을 위해 다음 영업일 조회
        int firstFallBackDay = firstResult.getSecond();
        LogUtils.showInfo(this.getClass(), "✅", "%s 경제 지표 조회 완료!", startDate.minusDays(firstFallBackDay));
        LocalDate nextStartDate = startDate.minusDays(firstFallBackDay + 1); // 첫 번째 조회일보다 하루 이후부터 조회

        // 두 번째 영업일 조회
        Pair<List<C>, Integer> secondResult = // 두 번째 영업일 조회
                getIndicator(fallbackDay -> callClientMethod.apply(nextStartDate.minusDays(fallbackDay)));

        Integer secondFallBackDay = secondResult.getSecond();
        LogUtils.showInfo(this.getClass(), "✅", "%s 경제 지표 조회 완료!", nextStartDate.minusDays(secondFallBackDay));

        // [4] 두 영업일 정보 기반 오늘 게시할 지표 계산 및 결과 반환
        return mappingMethod.apply(firstResult.getFirst(), secondResult.getFirst());
    }


    // 경제지표 API 조회 시도 (FallBack 일수까지)
    // C : Client 요청 결과 DTO 클래스, R : 반환 클래스
    private <C, R> List<R> getMarketIndicator(
            LocalDate startDate,
            Function<LocalDate, List<C>> callClientMethod,
            Function<List<C>, List<R>> mappingMethod) {

        // [1] 가장 최근 영업일의 지표 조회
        Pair<List<C>, Integer> firstResult =
                getIndicator(fallbackDay -> callClientMethod.apply(startDate.minusDays(fallbackDay)));

        // [2] 매핑 수행 및 반환
        LogUtils.showInfo(this.getClass(), "✅", "%s 주식 시장 지표 조회 완료!", startDate.minusDays(firstResult.getSecond()));
        return mappingMethod.apply(firstResult.getFirst());
    }



    // 주식 시장지표 API 조회 시도 (FallBack 일수까지)
    // R : 반환 클래스
    private <R> Pair<List<R>, Integer> getIndicator(
            Function<Integer, List<R>> callMethod) {

        // 총 MAX_FALLBACK_DAYS 까지 시도
        for (int fallbackDay = 0; fallbackDay <= MAX_FALLBACK_DAYS; fallbackDay++) {

            try {
                // fallback day를 계속 늘려가며 시도
                return Pair.of(callMethod.apply(fallbackDay), fallbackDay);

                // 만약 결과가 비어있는 경우엔 예외를 잡고 재시도 하도록 처리
            } catch (ProviderException e) {

                // 빈 결과로 인한 예외가 아니면, 예외 전파
                if (!Objects.equals(e.getAppStatus(), AppStatus.API_RESPONSE_EMPTY)) throw e;
            }
        }

        // Fallback 처리에 최종적으로 실패한 경우 로그 출력 후 예외 던짐
        LogUtils.showError(this.getClass(), "주식 시장 지표 Fallback 처리에 실패했습니다! 시도 Fallback 일수 : %d일", MAX_FALLBACK_DAYS);
        throw new BusinessException(AppStatus.INDICATOR_FALLBACK_FAILED);
    }

}


