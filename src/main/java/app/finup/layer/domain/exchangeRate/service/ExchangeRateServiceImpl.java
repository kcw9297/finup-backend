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

    @Override
    public List<ExchangeRateDto.Row> getLatestRates() {
        LocalDate searchDate = determineInitialSearchDate();

        for (int i = 0; i < MAX_FALLBACK_DAYS; i++) {

            List<ExchangeRateDto.Row> rows = apiClient.fetchRates(searchDate);

            if (isValid(rows)) {
                return rows;
            }

            searchDate = searchDate.minusDays(1);
        }

        throw new RuntimeException("환율 데이터 10일간 없음");
    }

    /** 평일/주말/11시 이전 판단 */
    private LocalDate determineInitialSearchDate() {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 주말 처리 → 금요일
        if (today.getDayOfWeek() == DayOfWeek.SATURDAY) return today.minusDays(1);
        if (today.getDayOfWeek() == DayOfWeek.SUNDAY) return today.minusDays(2);

        // 평일 11시 이전 → 어제
        if (now.isBefore(LocalTime.of(11, 0))) return today.minusDays(1);

        return today;
    }

    private boolean isValid(List<ExchangeRateDto.Row> rows) {
        return rows != null
                && !rows.isEmpty()
                && rows.get(0).getDealBasR() != null
                && !rows.get(0).getDealBasR().equals("null");
    }

    @Override
    public void updateRates() {
        LocalDate searchDate = determineInitialSearchDate();
        List<ExchangeRateDto.Row> apiRows = apiClient.fetchRates(searchDate);

        for (ExchangeRateDto.Row dto : apiRows) {
            String unit = dto.getCurUnit();
            if ("USD".equals(unit) || "JPY".equals(unit)) {
                ExchangeRate entity = ExchangeRateDtoMapper.toEntity(dto);
                repository.save(entity);
                log.info("환율 갱신: {} = {}", unit, dto.getDealBasR());
            }
        }
    }

    @Override
    public ExchangeRateDto.Row getRate(String currency) {
        ExchangeRate entity = repository.findByCurrency(currency)
                .orElseThrow(() -> new RuntimeException("환율 정보 없음: " + currency));

        return ExchangeRateDtoMapper.toRow(entity);
    }

    @Override
    public List<ExchangeRateDto.Row> getAllRates() {
        return repository.findAll().stream()
                .map(ExchangeRateDtoMapper::toRow)
                .toList();
    }
}