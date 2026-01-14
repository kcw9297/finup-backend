package app.finup.layer.domain.economicindicator.service;

import app.finup.api.external.indicator.client.IndicatorClient;
import app.finup.layer.domain.economicindicator.dto.ExchangeRateDto;
import app.finup.layer.domain.economicindicator.dto.ExchangeRateDtoMapper;
import app.finup.layer.domain.economicindicator.entity.ExchangeRate;
import app.finup.layer.domain.economicindicator.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final IndicatorClient indicatorClient;
    private final ExchangeRateRepository repository;
    private static final int MAX_FALLBACK_DAYS = 10;

    // 홈 화면용 최신 환율 조회
    @Override
    public List<ExchangeRateDto.Row> getLatestRates() {
        List<ExchangeRate> entities = repository.findByCurrencyIn(List.of("USD", "JPY", "JPY(100)"));

        if (entities.isEmpty()) {
            log.warn("DB에 환율 데이터 없음");
            return List.of();
        }
        return entities.stream()
                .map(ExchangeRateDtoMapper::toRow)
                .toList();
    }

    // 환율 자동 갱신
    @Override
    public void updateRates() {
        LocalDate baseDate = determineInitialSearchDate();
        LocalDate todayRateDate = null;
        List<ExchangeRateDto.ApiRow> todayRows = List.of();

        // today 기준 영업일 찾기 (최대 10일)
        for (int i = 0; i < MAX_FALLBACK_DAYS; i++) {
            LocalDate date = baseDate.minusDays(i);
            List<ExchangeRateDto.ApiRow> rows = apiClient.getExchangeRates(date);

            if (isValid(rows)) {
                todayRateDate = date;
                todayRows = rows;
                break;
            }
        }
        if (todayRateDate == null) {
            log.error("환율 갱신 실패: today 기준 데이터 없음");
            return;
        }

        // yesterday 기준 영업일 찾기
        LocalDate yesterdayRateDate = null;
        List<ExchangeRateDto.ApiRow> yesterdayRows = List.of();

        for (int i = 1; i <= MAX_FALLBACK_DAYS; i++) {
            LocalDate date = todayRateDate.minusDays(i);
            List<ExchangeRateDto.ApiRow> rows = apiClient.getExchangeRates(date);

            if (isValid(rows)) {
                yesterdayRateDate = date;
                yesterdayRows = rows;
                break;
            }
        }

        // yesterdayRate 매핑
        Map<String, Double> yesterdayMap = new HashMap<>();
        for (ExchangeRateDto.ApiRow r : yesterdayRows) {
            if (isTargetCurrency(r.getCurUnit())) {
                yesterdayMap.put(
                        r.getCurUnit(),
                        parseRate(r.getDealBasR())
                );
            }
        }

        // DB 반영
        for (ExchangeRateDto.ApiRow r : todayRows) {
            String unit = r.getCurUnit();
            if (!isTargetCurrency(unit)) continue;

            double todayRate = parseRate(r.getDealBasR());
            double yesterdayRate = yesterdayMap.getOrDefault(unit, todayRate);

            Optional<ExchangeRate> optional = repository.findByCurrency(unit);
            ExchangeRate entity;
            if (optional.isPresent()) {
                entity = optional.get();
            } else {
                entity = ExchangeRateDtoMapper.toNewEntity(r, todayRateDate);
            }
            entity.update(todayRate, yesterdayRate, todayRateDate);
            repository.save(entity);
        }
        log.info(
                "환율 갱신 완료 (today={}, yesterday={})",
                todayRateDate, yesterdayRateDate
        );
    }

    // 단일 통화 조회 (DB)
    @Override
    public ExchangeRateDto.Row getRate(String currency) {
        ExchangeRate entity = repository.findByCurrency(currency)
                .orElseThrow(() ->
                        new RuntimeException("환율 정보 없음: " + currency)
                );
        return ExchangeRateDtoMapper.toRow(entity);
    }

    // 전체 환율 조회 (DB)
    @Override
    public List<ExchangeRateDto.Row> getAllRates() {
        return repository.findAll().stream()
                .map(ExchangeRateDtoMapper::toRow)
                .toList();
    }

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