package app.finup.layer.domain.stock.service;

import app.finup.infra.news.provider.NewsProvider;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.stock.api.StockApiClient;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.dto.StockDtoMapper;
import app.finup.layer.domain.stock.entity.Stock;
import app.finup.layer.domain.stock.redis.StockStorage;
import app.finup.layer.domain.stock.repository.StockRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileInputStream;
import java.util.*;

/**
 * StockService 구현 클래스
 * @author lky
 * @since 2025-12-03
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockApiClient stockApiClient;
    private final NewsProvider newsProvider;
    private final StockStorage stockStorage;

    // 종목+ 시가총액 순위 가져오기
    @Override
    public List<StockDto.MarketCapRow> getMarketCapRow() {

        List<StockDto.MarketCapRow> marketCapRowList = stockStorage.getMarketCapRow();
        if (marketCapRowList.isEmpty()){
            refreshMarketCapRow();
            marketCapRowList = stockStorage.getMarketCapRow();
            if(marketCapRowList.isEmpty()) return Collections.emptyList(); //redis 오류로 인한 null 방지
        }else{
            log.info("시가총액 리스트 Redis에서 가져옴");
        }
        return marketCapRowList;
    }

    @Override
    public void refreshMarketCapRow(){
        List<StockDto.MarketCapRow> marketCapRowList = stockApiClient.fetchMarketCapRow();
        stockStorage.setMarketCapRow(marketCapRowList);
        log.info("시가총액 리스트 갱신발급함");
    }

    // 종목+ 거래대금 순위 가져오기
    @Override
    public List<StockDto.TradingValueRow> getTradingValueRow() {

        List<StockDto.TradingValueRow> tradingValueRowList = stockStorage.getTradingValueRow();
        if (tradingValueRowList.isEmpty()){
            refreshTradingValueRow();
            tradingValueRowList = stockStorage.getTradingValueRow();
            if(tradingValueRowList.isEmpty()) return Collections.emptyList(); //redis 오류로 인한 null 방지
        }else{
            log.info("거래대금 리스트 Redis에서 가져옴");
        }
        return tradingValueRowList;
    }

    @Override
    public void refreshTradingValueRow(){
        List<StockDto.TradingValueRow> tradingValueRowList = stockApiClient.fetchTradingValueRow();
        stockStorage.setTradingValueRow(tradingValueRowList);
        log.info("거래대금 리스트 갱신발급함");
    }

    // kospi_code.xlsx에서 종목코드, 종목명 읽어 DB 저장
    @Override
    public void importKospi() throws Exception {
        /*
        * 개발 중이라면 프로젝트 안 폴더에 두고 상대경로로 테스트
        * 운영/배포용이라면 외부 경로 + 환경변수로 관리하는 것이 안전
        * */
        String path = "src/main/java/app/finup/layer/domain/stock/test/kospi_code.xlsx"; // 이거 나중에 공통 파일 저장 경로로 바꾸고 파일도 거기에 옮겨두기
        //String path = "src/main/java/app/finup/layer/domain/stock/test/kospi_code.xlsx";
        //String path = "D:/GOLD/FinUp/data/kospi_code.xlsx"; // 이거 나중에 공통 파일 저장 경로로 바꾸고 파일도 거기에 옮겨두기
        //String path = "C:/Users/컴퓨터/Desktop/FinUp/data/kospi_code.xlsx";

        FileInputStream fis = new FileInputStream(path);
        //InputStream is = getClass().getClassLoader().getResourceAsStream("test/kospi_code.xlsx");
        if(fis==null){
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
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
    public StockDto.Detail getDetail(String code) {

        StockDto.Detail detail = stockStorage.getDetail(code);
        if (detail == null){
            refreshDetail(code);
            detail = stockStorage.getDetail(code);
        }else{
            log.info("종목 상세 정보 Redis에서 가져옴");
        }
        return detail;
    }

    @Override
    public void refreshDetail(String code){
        //System.out.println("요청 종목코드: [" + code + "]");

        //[1] 종목코드(String code)로 DB에서 한글 종목명 가져오기
        Stock stock;
        try {
            if (!stockRepository.existsByMkscShrnIscd(code)) {
                if (stockRepository.count() == 0) importKospi();
            }
            stock = stockRepository.findByMkscShrnIscd(code)
                    .orElseThrow(() -> new RuntimeException("종목 없음: " + code));
        } catch (Exception e) {
            log.error("종목 상세 refresh 실패 - code={}", code, e);
            throw new RuntimeException("종목 정보 조회/갱신 실패", e);
        }
        String htsKorIsnm = stock.getHtsKorIsnm();

        /*
        Stock stock = stockRepository.findByMkscShrnIscd(code)
                .orElseThrow(() -> new RuntimeException("종목 없음!"));
        String htsKorIsnm = stock.getHtsKorIsnm();*/

        //System.out.println("종목이름: " + htsKorIsnm);

        //[2] api 호출, parsing
        JsonNode jsonNode = stockApiClient.fetchDetail(code);

        //[3] dto 매핑하기
        StockDto.Detail detail = StockDtoMapper.toDetail(htsKorIsnm, jsonNode);

        //[4] Redis 저장하기
        stockStorage.setDetail(code, detail);

        log.info("종목 상세 정보 갱신발급함 code={}", code);

    }

    @Override
    public List<NewsDto.Row> getStockNews(String stockName, String category) {
        return newsProvider.getStockNews(stockName, category, 50);
    }
}

