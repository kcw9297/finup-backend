package app.finup.layer.domain.stockChart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Service
@RequiredArgsConstructor
public class StockChartServiceImpl implements StockChartService {
    @Qualifier("kisClient")
    private final WebClient kisClient;

    @Value("${api.kis.local.access-token}")
    private String token;

    @Override
    public String inquireDaily(String code) {
        return kisClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-daily-price")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", code)
                        .queryParam("FID_PERIOD_DIV_CODE", "D")
                        .queryParam("FID_ORG_ADJ_PRC", "0")
                        .build())
                .header("authorization", "Bearer " + token)
                .header("tr_id", "FHKST01010400")
                .header("custtype", "P")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
