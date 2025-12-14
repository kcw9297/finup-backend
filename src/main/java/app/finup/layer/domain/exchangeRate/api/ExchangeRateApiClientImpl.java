package app.finup.layer.domain.exchangeRate.api;

import app.finup.layer.domain.exchangeRate.dto.ExchangeRateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateApiClientImpl implements ExchangeRateApiClient {
    // 한국수출입은행 API 전용 WebClient
    @Qualifier("keximClient")
    private final WebClient keximClient;

    // 수출입은행 인증 키
    @Value("${API_KEXIM_KEY}")
    private String keximKey;

    // API 요청용 날짜 포맷 (yyyyMMdd)
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    // 특정 날짜의 환율 목록 조회
    @Override
    public List<ExchangeRateDto.ApiRow> fetchRates(LocalDate searchDate) {
        try {
            String json = keximClient.get()
                    .uri(uri -> uri
                            .queryParam("authkey", keximKey)
                            .queryParam("searchdate", searchDate.format(FMT))
                            .queryParam("data", "AP01")
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // JSON → DTO 변환
            return parseKeximJson(json);
        } catch (Exception e) {
            log.error("KEXIM API 호출 실패: {}", e.getMessage());
            return List.of();
        }
    }

    // 수출입은행 API JSON 응답 파싱
    private List<ExchangeRateDto.ApiRow> parseKeximJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ExchangeRateDto.ApiRow[] rows =
                    mapper.readValue(json, ExchangeRateDto.ApiRow[].class);
            return Arrays.asList(rows);
        } catch (Exception e) {
            log.error("JSON 파싱 실패: {}", e.getMessage());
            return List.of();
        }
    }
}