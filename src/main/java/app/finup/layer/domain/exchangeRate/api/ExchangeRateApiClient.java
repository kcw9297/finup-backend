package app.finup.layer.domain.exchangeRate.api;

import app.finup.layer.domain.exchangeRate.dto.ExchangeRateDto;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateApiClient {
    // 수출입은행 API 호출 및 JSON → DTO 매핑
    List<ExchangeRateDto.ApiRow> fetchRates(LocalDate searchDate);
}