package app.finup.layer.domain.exchangeRate.service;

import app.finup.layer.domain.exchangeRate.api.ExchangeRateApiClient;
import app.finup.layer.domain.exchangeRate.dto.ExchangeRateDto;
import app.finup.layer.domain.exchangeRate.dto.ExchangeRateDtoMapper;
import app.finup.layer.domain.exchangeRate.entity.ExchangeRate;
import app.finup.layer.domain.exchangeRate.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateApiClient apiClient;
    private final ExchangeRateRepository repository;

    private static final int MAX_FALLBACK_DAYS = 10;

    /**
     * 홈 화면용 최신 환율 조회
     * - 외부 API 호출 ❌
     * - DB 데이터만 사용
     */
    @Override
    public List<ExchangeRateDto.Row> getLatestRates() {

        List<ExchangeRate> entities = repository.findAll();

        if (entities.isEmpty()) {
            log.warn("DB에 환율 데이터 없음");
            return List.of();
        }

        return entities.stream()
                .map(ExchangeRateDtoMapper::toRow)
                .toList();
    }

    /**
     * 환율 자동 갱신
     * - 서버 시작 시 1회
     * - 평일 11시 스케줄 실행
     * - 외부 API 호출 + 10일 fallback
     * - DB에 today / yesterday 갱신
     */
    @Override
    public void updateRates() {

        LocalDate baseDate = determineInitialSearchDate();

        for (int i = 0; i < MAX_FALLBACK_DAYS; i++) {

            LocalDate targetDate = baseDate.minusDays(i);
            List<ExchangeRateDto.ApiRow> apiRows =
                    apiClient.fetchRates(targetDate);

            if (!isValid(apiRows)) {
                log.info("환율 fallback 실패: {}", targetDate);
                continue;
            }

            log.info("환율 갱신 기준일: {}", targetDate);

            for (ExchangeRateDto.ApiRow apiRow : apiRows) {

                String unit = apiRow.getCurUnit();
                if (!isTargetCurrency(unit)) continue;

                double todayRate = parseRate(apiRow.getDealBasR());

                Optional<ExchangeRate> optional =
                        repository.findByCurrency(unit);

                if (optional.isPresent()) {
                    // 기존 통화 → today → yesterday 이동 후 갱신
                    optional.get().update(todayRate, targetDate);
                } else {
                    // 최초 저장 (보합 처리)
                    repository.save(ExchangeRateDtoMapper.toNewEntity(apiRow, targetDate));
                }
            }
            return; // 성공 시 종료
        }

        log.error("환율 갱신 실패: 10일간 유효 데이터 없음");
    }

    /**
     * 단일 통화 조회 (DB)
     */
    @Override
    public ExchangeRateDto.Row getRate(String currency) {
        ExchangeRate entity = repository.findByCurrency(currency)
                .orElseThrow(() ->
                        new RuntimeException("환율 정보 없음: " + currency)
                );
        return ExchangeRateDtoMapper.toRow(entity);
    }

    /**
     * 전체 환율 조회 (DB)
     */
    @Override
    public List<ExchangeRateDto.Row> getAllRates() {
        return repository.findAll().stream()
                .map(ExchangeRateDtoMapper::toRow)
                .toList();
    }

    /* ===================== 내부 유틸 메소드 ===================== */

    // 조회 기준 날짜 계산 (주말 / 11시 이전 보정)
    private LocalDate determineInitialSearchDate() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (today.getDayOfWeek() == DayOfWeek.SATURDAY) return today.minusDays(1);
        if (today.getDayOfWeek() == DayOfWeek.SUNDAY) return today.minusDays(2);
        if (now.isBefore(LocalTime.of(11, 0))) return today.minusDays(1);

        return today;
    }

    // 외부 API 응답 유효성 검사
    private boolean isValid(List<ExchangeRateDto.ApiRow> rows) {
        return rows != null
                && !rows.isEmpty()
                && rows.get(0).getDealBasR() != null
                && !"null".equals(rows.get(0).getDealBasR());
    }

    // 필요한 통화만 필터
    private boolean isTargetCurrency(String unit) {
        return "USD".equals(unit)
                || "JPY".equals(unit)
                || "JPY(100)".equals(unit);
    }

    // 콤마 제거 후 double 변환
    private double parseRate(String rate) {
        return Double.parseDouble(rate.replace(",", ""));
    }
}