package app.finup.layer.domain.stocks.service;

import app.finup.layer.domain.stocks.dto.StocksDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

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

    /*api URL*/
    String detail = "/uapi/domestic-stock/v1/quotations/inquire-price";

    // 종목 상세 페이지 데이터 가져오기
    @Override
    public List<StocksDto.Detail> getDetail(String code) {

        //[1] 종목코드(String code)로 json 데이터 가져오기
        String json = fetchDetailJson(code);
        if(json == null) return List.of();

        System.out.println(json);
        log.info(json);

        //List<StocksDto.Detail> list = parseDetailJson(json);

        //return list;
        return null;
    }

    private String fetchDetailJson(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.koreainvestment.com:9443")
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(detail)
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
}

/*
content-type: str    #컨텐츠타입
authorization: str    #접근토큰
appkey: str    #앱키
appsecret: str    #앱시크릿키
personalseckey: Optional[str] = None    #고객식별키
tr_id: str    #거래ID
tr_cont: Optional[str] = None    #연속 거래 여부
custtype: str    #고객 타입
seq_no: Optional[str] = None    #일련번호
mac_address: Optional[str] = None    #맥주소
phone_number: Optional[str] = None    #핸드폰번호
ip_addr: Optional[str] = None    #접속 단말 공인 IP
gt_uid: Optional[str] = None    #Global UID
*/

