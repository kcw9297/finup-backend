package app.finup.layer.domain.economicindicator.service;

import app.finup.layer.domain.economicindicator.dto.ExchangeRateDto;
import java.util.List;

public interface ExchangeRateService {
    // 최신 환율
    List<ExchangeRateDto.Row> getLatestRates();

    // 수출입은행 API 호출 후 DB에 최신 환율 저장
    void updateRates();

    // 단일 통화 조회
    ExchangeRateDto.Row getRate(String currency);

    // 전체 환율 조회
    List<ExchangeRateDto.Row> getAllRates();
}