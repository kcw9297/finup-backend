package app.finup.layer.domain.stock.service;

import app.finup.layer.domain.stock.dto.StocksDto;
import app.finup.layer.domain.stock.dto.StocksDtoMapper;
import app.finup.layer.domain.stock.entity.Stock;
import app.finup.layer.domain.stock.repository.StockRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;

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
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

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


    // kospi_code.xlsx에서 종목코드, 종목명 읽어 DB 저장
    @Override
    public void importKospi() throws Exception {
        /*
        * 개발 중이라면 프로젝트 안 폴더에 두고 상대경로로 테스트
        * 운영/배포용이라면 외부 경로 + 환경변수로 관리하는 것이 안전
        * */
        String path = "D:/GOLD/FinUp/data/kospi_code.xlsx"; // 이거 나중에 공통 파일 저장 경로로 바꾸고 파일도 거기에 옮겨두기
        //String path = "C:/Users/컴퓨터/Desktop/FinUp/data/kospi_code.xlsx";

        FileInputStream fis = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // 헤더 스킵

            String mkscShrnIscd = row.getCell(0).getStringCellValue();
            String htsKorIsnm = row.getCell(2).getStringCellValue();

            Stock stock = Stock.builder()
                    .mkscShrnIscd(mkscShrnIscd)
                    .htsKorIsnm(htsKorIsnm)
                    .build();

            stockRepository.save(stock);
        }
        workbook.close();
    }

    // 종목 상세 페이지 데이터 가져오기
    @Override
    public StocksDto.Detail getDetail(String code) {
        //System.out.println("요청 종목코드: [" + code + "]");

        //[1] 종목코드(String code)로 DB에서 한글 종목명 가져오기
        Stock stock = stockRepository.findByMkscShrnIscd(code)
                .orElseThrow(() -> new RuntimeException("종목 없음!"));
        String htsKorIsnm = stock.getHtsKorIsnm();
        //System.out.println("종목이름: " + htsKorIsnm);

        //[2] 종목코드(String code)로 api json 데이터 가져오기
        String json = fetchDetailJson(code);
        if(json == null) return null;

        //System.out.println(json);
        //log.info(json);

        //[3] json데이터 dto로 parsing하기
        JsonNode jsonNode = parseDetailJson(json);
        //StocksDto.Detail detail = parseDetailJson(json);
        //System.out.println(detail);

        //[4] dto 매핑하기
        StocksDto.Detail detail = StocksDtoMapper.toDetail(htsKorIsnm, jsonNode);

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

    private JsonNode parseDetailJson(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            return root.path("output");
            //return StocksDtoMapper.toDetail(output);
        } catch (Exception e) {
            log.error("상세 정보 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}

