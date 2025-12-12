package app.finup.layer.domain.stockChart.service;

import app.finup.layer.domain.stock.api.AuthStockApiClient;
import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.enums.CandleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class StockChartServiceImpl implements StockChartService {
    @Qualifier("kisClient")
    private final WebClient kisClient;
    private final AuthStockApiClient authStockApiClient;
    private final StockChartAiService stockChartAiService;

    @Override
    public StockChartDto.Row inquireDaily(String code, CandleType candleType) {
        String accessToken = authStockApiClient.getToken();
        String type = candleType.getKisCode();
        try {
            String json = kisClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/uapi/domestic-stock/v1/quotations/inquire-daily-price")
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", code)
                            .queryParam("FID_PERIOD_DIV_CODE", type)
                            .queryParam("FID_ORG_ADJ_PRC", "0")
                            .build())
                    .header("authorization", "Bearer " + accessToken)
                    .header("tr_id", "FHKST01010400")
                    .header("custtype", "P")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("RAW JSON: {}", json);
            //이중 json처리
            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = json.substring(1, json.length() - 1).replace("\\\"", "\"");
            }

            ObjectMapper om = new ObjectMapper();
            StockChartDto.Row row = om.readValue(json, StockChartDto.Row.class);
            //null 안전처리
            if(row.getOutput() == null){
                row.setOutput(new ArrayList<>());
                return row;
            }
            //날짜 오름차순(프론트 라이브러리 요구)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            row.getOutput().sort(Comparator.comparing(
                    d -> LocalDate.parse(d.getStck_bsop_date(), formatter)
            ));
            // MA 계산
            calculateMA(row.getOutput(), 5, false);
            calculateMA(row.getOutput(), 20, false);
            calculateMA(row.getOutput(), 60, false);

            // 거래량 이동평균
            calculateMA(row.getOutput(), 5, true);
            calculateMA(row.getOutput(), 20, true);

            return row;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void calculateMA(List<StockChartDto.Detail> list, int period, boolean isVolume){
        for(int i = 0; i < list.size(); i++){
            if(i+1<period) continue; //데이터가 충분한지 확인
            double sum = 0;
            for(int j = i - period + 1; j <= i; j++){
                sum += isVolume
                        ?Double.parseDouble(list.get(j).getAcml_vol())
                        :Double.parseDouble(list.get(j).getStck_clpr());
            }
            double ma = sum / period;

            if(isVolume){
                if(period == 5) list.get(i).setVolumeMa5(ma);
                if(period == 20) list.get(i).setVolumeMa20(ma);
            }else{
                if(period == 5) list.get(i).setMa5(ma);
                if(period == 20) list.get(i).setMa20(ma);
                if(period == 60) list.get(i).setMa60(ma);
            }
        }
    }
}
