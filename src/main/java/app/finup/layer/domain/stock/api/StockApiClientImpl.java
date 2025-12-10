package app.finup.layer.domain.stock.api;

import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.dto.StockDtoMapper;
import app.finup.layer.domain.stock.redis.AuthTokenStore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockApiClientImpl implements StockApiClient {
    // 이 부분 나중에 import해서 쓸거니까 지우기
   /*
    @Value("${API_KIS_LOCAL_ACCESS_TOKEN}")
    private String ACCESS_TOKEN;*/

    private final AuthStockApiClient authStockApiClient;
    private final WebClient kisClient;
    private final ObjectMapper objectMapper;

    /*api URI*/
    //종목 리스트 시가총액 순위
    public static final String MARKET_CAP = "/uapi/domestic-stock/v1/ranking/market-cap";
    //종목 상세 페이지 데이터
    public static final String DETAIL = "/uapi/domestic-stock/v1/quotations/inquire-price";

    //

    // 종목 상세페이지 시가총액 순위 가져오기
    @Override
    public List<StockDto.MarketCapRow> fetchMarketCapRow() {
        List<StockDto.MarketCapRow> list = new ArrayList<>();

        //[1] api 호출
        String path = MARKET_CAP;
        String trId = "FHPST01740000";

        Map<String, Object> params = new HashMap<>();
        params.put("FID_INPUT_PRICE_2", null);
        params.put("FID_COND_MRKT_DIV_CODE", "J");
        params.put("FID_COND_SCR_DIV_CODE", "20174");
        params.put("FID_DIV_CLS_CODE", "0");
        params.put("FID_INPUT_ISCD", "0000");
        params.put("FID_TRGT_CLS_CODE", "0");
        params.put("FID_TRGT_EXLS_CLS_CODE", "0");
        params.put("FID_INPUT_PRICE_1", null);
        params.put("FID_VOL_CNT", null);

        String json = callApi(path, trId, params);
        System.out.println("시가총액 api json 결과:" + json);

        //[2] json데이터 DTO parsing하기
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode outputs = jsonNode.path("output");

            for (JsonNode output : outputs) {
                list.add(StockDtoMapper.toMarketCapRow(output));
            }
        } catch (Exception e) {
            log.error("상세 정보 파싱 실패: {}", e.getMessage());
            return null;
        }
        return list;
    }
    private String callMarketCapApi() {
        //WebClient webClient = webClientConfig.kisClient();
        String accessToken = authStockApiClient.getToken();
        return kisClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(MARKET_CAP)
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .build()
                )
                //.header("appkey", APPKEY)
                //.header("appsecret", APPSECRET)
                .header("authorization", "Bearer " + accessToken)
                .header("tr_id", "FHPST01740000")
                .header("custtype", "P")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // 종목 상세 페이지 데이터 가져오기
    @Override
    public JsonNode fetchDetail(String code) {

        //[1] 종목코드(String code)로 api json 데이터 가져오기
        //String json = callDetailApi(code);
        String path = DETAIL;
        String trId = "FHKST01010100";
        Map<String, ?> params = Map.of(
                "FID_COND_MRKT_DIV_CODE", "J",
                "FID_INPUT_ISCD", code);
        String json = callApi(path, trId, params);

        //[2] json데이터 parsing하기
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.path("output");
        } catch (Exception e) {
            log.error("상세 정보 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    //api 호출 공통 메소드
    private String callApi(String path, String trId, Map<String, ?> params){
        //WebClient webClient = webClientConfig.kisClient();
        String accessToken = authStockApiClient.getToken();

        return kisClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(path);
                    if (params != null) {
                        params.forEach(builder::queryParam);
                    }
                    return builder.build();
                })
                /*
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", code)
                        .build()
                )*/
                //.header("appkey", APPKEY)
                //.header("appsecret", APPSECRET)
                .header("authorization", "Bearer " + accessToken)
                .header("tr_id", trId)
                .header("custtype", "P")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    //api parsing 공통 메소드
    private JsonNode parseJson(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.path("output");
            //return objectMapper.readTree(json);
        } catch (Exception e) {
            log.error("상세 정보 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

}
