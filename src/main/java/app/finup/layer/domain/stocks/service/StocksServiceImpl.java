package app.finup.layer.domain.stocks.service;

import app.finup.layer.domain.stocks.dto.StocksDto;
import app.finup.layer.domain.stocks.dto.StocksDtoMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * StocksService 구현 클래스
 * @author lky
 * @since 2025-12-03
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StocksServiceImpl implements StocksService {

    @Value("${API_KIS_CLIENT_ID}")
    private String APPKEY;

    @Value("${API_KIS_CLIENT_SECRET}")
    private String APPSECRET;

    @Value("${API_KIS_LOCAL_ACCESS_TOKEN}")
    private String ACCESS_TOKEN;

    private final ObjectMapper objectMapper;

    /*api URL*/
    //종목 상세 페이지 데이터
    public static final String DETAIL = "/uapi/domestic-stock/v1/quotations/inquire-price";

    //종목 리스트 시가총액 순위
    public static final String MARKET_CAP = "/uapi/domestic-stock/v1/ranking/market-cap";


    // 종목 상세 페이지 데이터 가져오기
    @Override
    public StocksDto.Detail getDetail(String code) {
        System.out.println("요청 종목코드: [" + code + "]");

        //[1] 종목코드(String code)로 json 데이터 가져오기
        String json = fetchDetailJson(code);
        if(json == null) return null;

        //System.out.println(json);
        //log.info(json);

        //[2] json데이터 dto로 parsing하기
        StocksDto.Detail detail = parseDetailJson(json);
        System.out.println(detail);

        return detail;
    }

    private String fetchDetailJson(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.koreainvestment.com:9443")
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DETAIL)
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", code)
                        .build()
                )
                .header("appkey", APPKEY)
                .header("appsecret", APPSECRET)
                .header("authorization", "Bearer " + ACCESS_TOKEN)
                .header("tr_id", "FHKST01010100")
                .header("custtype", "P")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private StocksDto.Detail parseDetailJson(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode output = root.path("output");
            return StocksDtoMapper.toDetail(output);
        } catch (Exception e) {
            log.error("상세 정보 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}

