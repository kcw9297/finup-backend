package app.finup.layer.domain.stock.service;

import app.finup.api.external.stock.dto.StockApiDto;
import app.finup.api.external.stock.client.StockClient;
import app.finup.infra.file.provider.XlsxProvider;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.redis.StockRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StockService 구현 클래스
 * @author kcw
 * @since 2025-12-25
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    // 사용 의존성
    private final StockClient stockClient;
    private final StockRedisStorage stockRedisStorage;
    private final FileStorage fileStorage;
    private final XlsxProvider xlsxProvider;

    // 사용 상수
    private static final List<String> STOCK_FILE_PATHS = List.of("base/kospi_code.xlsx", "base/kosdaq_code.xlsx");
    private static final int SHEET_START_INDEX = 0;
    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final List<String> EXTRACT_COLUMN_NAMES = List.of(CODE, NAME); // "code", "name" 컬럼 정보만 추출


    @Override
    public String issueToken() {

        // [1] 확인 - 이미 AT가 존재하는 경우 발급 미수행
        if (stockRedisStorage.isExistApiAccessToken()) return null;

        // [2] 주식 실시간 정보를 받기 위한 AT 발급 후, REDIS 내 저장
        StockApiDto.Issue rp = stockClient.issueToken();
        stockRedisStorage.storeApiAccessToken(rp.getAccessToken(), rp.getTtl());
        return rp.getAccessToken();
    }


    @Override
    public void initStockFile() {

        // [1] 이미 존재하는지 검증 (존재 시 미수행)
        if (stockRedisStorage.isExistStockName()) return;

        // [2] 주식 파일을 읽고, 읽은 결과를 모두 가진 Map 생성
        Map<String, String> codeNameMap = new ConcurrentHashMap<>();
        STOCK_FILE_PATHS.forEach(filepath -> codeNameMap.putAll(loadStocksFromFile(filepath)));

        // [3] Redis 내 정보 저장
        stockRedisStorage.storeStockCodeNames(codeNameMap);
    }


    // 파일 조회 후 Map<종목코드, 종목명> 추출
    private Map<String, String> loadStocksFromFile(String filepath) {

        // [1] Excel에서 컬럼 데이터 추출
        byte[] fileBytes = fileStorage.download(filepath); // 파일 다운로드
        Map<String, List<String>> columnData =
                xlsxProvider.extractColumns(fileBytes, SHEET_START_INDEX, EXTRACT_COLUMN_NAMES);

        // [2] 종목코드 - 종목명 매핑
        Map<String, String> result = new ConcurrentHashMap<>();
        List<String> codes = columnData.get(CODE);
        List<String> names = columnData.get(NAME);

        // [3] 결과 Map에 저장 후 반환
        int size = Math.min(codes.size(), names.size());
        for (int i = 0; i < size; i++) result.put(codes.get(i), names.get(i));
        return result;
    }


    @Override
    public List<StockDto.Info> getMarketCapList() {
        return stockRedisStorage.getMarketCapStockInfos();
    }


    @Override
    public List<StockDto.Info> getTradingValueList() {
        return stockRedisStorage.getTradingValueStockInfos();
    }

}


