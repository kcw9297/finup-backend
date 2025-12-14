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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final ExchangeRateApiClient apiClient;
    private final ExchangeRateRepository repository;

    // 최신 환율
    @Override
    public List<ExchangeRateDto.Row> getLatestRates() {

        // 기준 날짜 계산 (주말, 11시 이전 보정)
        LocalDate todayDate = determineInitialSearchDate();
        LocalDate yesterdayDate = todayDate.minusDays(1);

        // 오늘 / 어제 환율 조회
        List<ExchangeRateDto.ApiRow> todayRows = apiClient.fetchRates(todayDate);
        List<ExchangeRateDto.ApiRow> yesterdayRows = apiClient.fetchRates(yesterdayDate);

        if (!isValid(todayRows)) {
            log.warn("오늘 환율 데이터 없음");
            return List.of();
        }

        // 어제 환율을 통화코드 기준 Map으로 변환
        Map<String, Double> yesterdayMap = yesterdayRows.stream()
                .filter(r -> isTargetCurrency(r.getCurUnit()))
                .filter(r -> r.getDealBasR() != null)
                .collect(Collectors.toMap(
                        ExchangeRateDto.ApiRow::getCurUnit,
                        r -> parseRate(r.getDealBasR()),
                        (a, b) -> a
                ));
        String updatedAt = LocalDateTime.now().toString();

        // 오늘 환율 + 어제 환율 → 응답 DTO 생성
        return todayRows.stream()
                .filter(r -> isTargetCurrency(r.getCurUnit()))
                .filter(r -> r.getDealBasR() != null)
                .map(r -> {
                    double today = parseRate(r.getDealBasR());
                    double yesterday = yesterdayMap.getOrDefault(r.getCurUnit(), today);

                    return ExchangeRateDto.Row.builder()
                            .curUnit(r.getCurUnit())
                            .curNm(r.getCurNm())
                            .today(today)
                            .yesterday(yesterday)
                            .updatedAt(updatedAt)
                            .build();
                })
                .toList();
    }

    // 환율 조회 기준 날짜 계산
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

    // 필요한 통화만 필터 (USD / JPY)
    private boolean isTargetCurrency(String unit) {
        return "USD".equals(unit) || "JPY".equals(unit) || "JPY(100)".equals(unit);
    }

    // 콤마 제거 후 double 변환
    private double parseRate(String rate) {
        return Double.parseDouble(rate.replace(",", ""));
    }

    // 최신 환율 DB 저장
    @Override
    public void updateRates() {
        LocalDate searchDate = determineInitialSearchDate();
        List<ExchangeRateDto.ApiRow> apiRows = apiClient.fetchRates(searchDate);

        for (ExchangeRateDto.ApiRow apiRow : apiRows) {
            if (isTargetCurrency(apiRow.getCurUnit())) {
                ExchangeRate entity = ExchangeRateDtoMapper.toEntity(apiRow);
                repository.save(entity);
            }
        }
    }

    // 단일 통화 조회
    @Override
    public ExchangeRateDto.Row getRate(String currency) {
        ExchangeRate entity = repository.findByCurrency(currency)
                .orElseThrow(() -> new RuntimeException("환율 정보 없음: " + currency));
        return ExchangeRateDtoMapper.toRow(entity);
    }

    // 전체 환율 조회
    @Override
    public List<ExchangeRateDto.Row> getAllRates() {
        return repository.findAll().stream()
                .map(ExchangeRateDtoMapper::toRow)
                .toList();
    }
}