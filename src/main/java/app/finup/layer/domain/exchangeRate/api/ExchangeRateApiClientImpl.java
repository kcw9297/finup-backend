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
    @Qualifier("keximClient")
    private final WebClient keximClient;

    @Value("${API_KEXIM_KEY}")
    private String apiKeximKey;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public List<ExchangeRateDto.Row> fetchRates(LocalDate searchDate) {
        try {
            String json = keximClient.get()
                    .uri(uri -> uri
                            .queryParam("authkey", apiKeximKey)
                            .queryParam("searchdate", searchDate.format(FMT))
                            .queryParam("data", "AP01")
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return parseKeximJson(json);
        }catch (Exception e){
            log.error("KEXIM API 호출 실패: {}", e.getMessage());
            return List.of();
        }
    }

    private List<ExchangeRateDto.Row> parseKeximJson(String json){
        try {
            ObjectMapper mapper = new ObjectMapper();
            ExchangeRateDto.Row[] rows =
                    mapper.readValue(json, ExchangeRateDto.Row[].class);
            return Arrays.asList(rows);
        } catch (Exception e) {
            log.error("JSON 파싱 실패: {}", e.getMessage());
            return List.of();
        }
    }
}