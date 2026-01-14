package app.finup.layer.domain.stock.redis;

import app.finup.layer.domain.stock.dto.StockAiDto;
import app.finup.layer.domain.stock.dto.StockDto;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 주식 관련 정보를 Redis와 직접 조작하는 기능 제공 Storage 인터페이스
 * @author kcw
 * @since 2025-12-25
 */
public interface StockRedisStorage {

    /**
     * 주식 코드에 대응하는 한글 주식명 정보 목록 저장 (영구 저장)
     * @param codeNameMap Map<주식코드, 주식한글명> 정보
     */
    void storeStockCodeNames(Map<String, String> codeNameMap);


    /**
     * 현재 저장소에 주식코드-주식한글명 정보 목록이 존재하는지 확인
     * @return 존재하지 않은 경우 false, 이미 존재하면 true
     */
    boolean isExistStockName();


    /**
     * 현재 저장소에 주식코드-주식한글명 정보 목록이 존재하지 않는지 확인
     * @param accessToken 주식 API로부터 발급받은 AT
     * @param ttl 발급받은 AT의 지속시간 (Time To Live)
     */
    void storeApiAccessToken(String accessToken, Duration ttl);


    /**
     * API에서 발급받은 AT 조회
     * @return AT 문자열
     */
    String getApiAccessToken();


    /**
     * API에서 발급받은 AT가 존재하는지 확인
     * @return 존재하지 않은 경우 false, 이미 존재하면 true
     */
    boolean isExistApiAccessToken();


    /**
     * API에서 조회한 주식 정보 일괄 저장
     */
    void storeStockInfos(List<StockDto.Info> stockInfos);


    /**
     * 주식 코드에 대응하는 한글 주식명 조회
     * @param stockCode 주식 고유 코드
     * @return 주식 코드에 대응하는 주식 정보 DTO
     */
    StockDto.Info getStockInfo(String stockCode);


    /**
     * 주식 코드에 대응하는 한글 주식명 조회
     * @return 현재 모든 주식 종목 정보 DTO 목록
     */
    List<StockDto.Info> getAllStockInfos();


    /**
     * 시가총액 순 주석정보 목록 일괄 조회
     */
    List<StockDto.Info> getMarketCapStockInfos();


    /**
     * 시가총액 순 주석정보 목록 일괄 조회
     */
    List<StockDto.Info> getTradingValueStockInfos();


    /**
     * 이전 종목 차트 분석 데이터 기록
     * @param stockCode     분석 대상 주식코드
     * @param memberId      요청 회원번호
     * @param analyzation   이전 주식 분석 데이터
     */
    void storePrevChartAnalyze(String stockCode, Long memberId, StockAiDto.ChartAnalyzation analyzation);


    /**
     * 이전 종목 차트 분석 기록 조회
     * @param stockCode 분석 대상 주식코드
     * @param memberId  요청 회원번호
     * @return 이전 분석정보 DTO
     */
    StockAiDto.ChartAnalyzation getPrevChartAnalyze(String stockCode, Long memberId);


    /**
     * 이전 종목 상세 분석 데이터 기록
     * @param stockCode     분석 대상 주식코드
     * @param memberId      요청 회원번호
     * @param analyzation   이전 주식 분석 데이터
     */
    void storePrevDetailAnalyze(String stockCode, Long memberId, StockAiDto.DetailAnalyzation analyzation);


    /**
     * 이전 종목 분석 기록 조회
     * @param stockCode 분석 대상 주식코드
     * @param memberId  요청 회원번호
     * @return 이전 분석정보 DTO
     */
    StockAiDto.DetailAnalyzation getPrevDetailAnalyze(String stockCode, Long memberId);


    /**
     * 이전에 추천된 영상 번호 기록
     * @param stockCode 분석 대상 주식코드
     * @param memberId  요청 회원번호
     * @param videoIds  추천된 영상번호 목록
     */
    void storePrevRecommendedVideoIds(String stockCode, Long memberId, List<String> videoIds);


    /**
     * 이전에 추천된 영상 이력 조회 (영상번호 목록)
     * @param stockCode 분석 대상 주식코드
     * @param memberId  요청 회원번호
     * @return 이전 추천이력 ID 목록
     */
    List<String> getPrevRecommendedVideoIds(String stockCode, Long memberId);

}
