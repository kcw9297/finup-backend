package app.finup.api.external.stock.client;

import app.finup.api.utils.ApiError;
import app.finup.api.utils.ApiRetry;
import app.finup.api.utils.ApiUtils;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.api.external.stock.dto.StockApiDto;
import app.finup.api.external.stock.dto.StockApiDtoMapper;
import app.finup.api.external.stock.enums.CandleType;
import app.finup.api.external.stock.utils.KisParamBuilder;
import app.finup.common.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * KIS API 기반 주식정보 제공 StockProvider 구현체
 * @author kcw
 * @since 2025-12-25
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class KisStockClient implements StockClient {

    // 사용 의존성
    private final WebClient kisAuthClient;
    private final WebClient kisClient;

    // 사용 상수
    @Value("${api.kis.client.id}")
    private String key;

    @Value("${api.kis.client.secret}")
    private String secret;

    // 주식 검색에 필요한 tr_id 정보
    private static final String TR_ID_MARKET_CAP = "FHPST01740000";
    private static final String TR_ID_TRADING_VALUE = "FHPST01710000";
    private static final String TR_ID_DETAIL = "FHKST01010100";
    private static final String TR_ID_CHART = "FHKST03010100";

    // 차트 파라미터 상수
    private static final String CHART_PRICE_ADJUST_TYPE_ADJUSTED = "0"; // 수정주가 (액면분할/병합, 유상증자 등의 이벤트 반영가)
    private static final String CHART_PRICE_ADJUST_TYPE_ORIGINAL = "1"; // 원주가 (실제 거래된 가격 그대로 표시)
    private static final int CHART_INTERVAL_DAY = 6; // 단위 : 달
    private static final int CHART_INTERVAL_WEEK = 3; // 단위 : 년
    private static final int CHART_INTERVAL_MONTH = 10; // 단위 : 년


    // URL 상수
    private static final String URL_AUTH = "/oauth2/tokenP"; // 토큰 요청
    private static final String URL_LIST_MARKET_CAP = "/uapi/domestic-stock/v1/ranking/market-cap"; // 시가총액 순위
    private static final String URL_LIST_TRADING_VALUE = "/uapi/domestic-stock/v1/quotations/volume-rank"; // 거래량 순위
    private static final String URL_DETAIL = "/uapi/domestic-stock/v1/quotations/inquire-price"; // 종목 상세
    private static final String URL_CHART = "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice"; // 종목 차트

    // API 요청 관련 상수
    private static final Duration TIMEOUT_ISSUE = Duration.ofSeconds(15);
    private static final Duration TIMEOUT_GET = Duration.ofSeconds(5);
    private static final int RETRY_MAX_ATTEMPTS = 5;
    private static final Duration RETRY_MIN_BACKOFF = Duration.ofMillis(700);
    private static final Duration RETRY_MAX_BACKOFF = Duration.ofMillis(3000);
    private static final Double RETRY_JITTER = 0.75;


    //kis 접근 토큰 갱신하기
    @Override
    public StockApiDto.Issue issueToken() {

        // [1] 요청 Body에 담을 데이터 선언
        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "appkey", key,
                "appsecret", secret
        );


        // [2] API 요청 및 DTO 변환 & 반환
        return kisAuthClient.post()

                // [1] API 요청 전송
                .uri(URL_AUTH)
                .bodyValue(body)
                .retrieve()

                // [2] 요청 결과 JSON 문자열로 변환
                .bodyToMono(String.class)
                .timeout(TIMEOUT_ISSUE) // 요청 Timeout
                .retryWhen(setRetrySpec()) // 실패 시 재시도 로직
                .switchIfEmpty(Mono.error(new ProviderException(AppStatus.API_STOCK_AT_ISSUE_FAILED))) // 결과가 빈 경우 처리

                // [3] 토큰 존재 검증
                .map(json -> StrUtils.fromJson(json, StockApiDto.IssueRp.class)) // JSON 형태 그대로 DTO로 변환
                .filter(rp -> Objects.nonNull(rp.getAccessToken()) && !rp.getAccessToken().isBlank()) // 토큰 정보가 없는 경우 필터
                .switchIfEmpty(Mono.error(new ProviderException(AppStatus.API_STOCK_AT_ISSUE_FAILED))) // 토큰 결과가 없는 경우 예외 처리

                // [4] 최종적으로 제공할 DTO로 변환 (필요 데이터만 추출)
                .map(StockApiDtoMapper::toIssue)

                // [5] 예외 통합 처리
                .onErrorMap(Exception.class, throwError("기타 사유로 KIS API AccessToken 발급 시도 실패."))

                // [6] 최종 결과 생성 (변환 결과)
                .block();
    }


    @Override
    public List<StockApiDto.MarketCapRow> getMarketCapRankList(String accessToken) {

        return doGetStockRequest(
                URL_LIST_MARKET_CAP, // 요청 URL
                setMarketCapParamMap(), // URL 파라미터
                accessToken, // API에서 발급받은 AT
                TR_ID_MARKET_CAP, // TrId
                StockApiDto.MarketCapListRp.class, // JSON 문자열 구조 그대로 매핑할 DTO Class
                StockApiDtoMapper::toMarketCapRows // 실제 사용자에게 반환할 DTO를 매핑하는 메소드
        );
    }


    // 주식 시가총액순 리스트 검색을 위한 파라미터
    private MultiValueMap<String, String> setMarketCapParamMap() {

        return KisParamBuilder.builder()
                .marketCode("J")
                .screenCode("20174")
                .classCode("0")
                .stockCode("0000")
                .targetClassCode("0")
                .targetExcludeClassCode("0")
                .buildForMarketCap();
    }


    @Override
    public List<StockApiDto.TradingValueRow> getTradingValueList(String accessToken) {

        return doGetStockRequest(
                URL_LIST_TRADING_VALUE, // 요청 URL
                setTradingValueParamMap(), // URL 파라미터
                accessToken, // API에서 발급받은 AT
                TR_ID_TRADING_VALUE, // TrId
                StockApiDto.TradingValueListRp.class, // JSON 문자열 구조 그대로 매핑할 DTO Class
                StockApiDtoMapper::toTradingValueRows // 실제 사용자에게 반환할 DTO를 매핑하는 메소드
        );
    }


    // 주식 거래대금순 리스트 검색을 위한 파라미터
    private MultiValueMap<String, String> setTradingValueParamMap() {

        return KisParamBuilder.builder()
                .marketCode("J")
                .screenCode("20171")
                .classCode("0")
                .stockCode("0000")
                .targetClassCode("111111111")
                .targetExcludeClassCode("0000000000")
                .belongClassCode("3")
                .buildForTradingValue();
    }


    @Override
    public StockApiDto.Detail getDetail(String code, String accessToken) {

        return doGetStockRequest(
                URL_DETAIL, // 요청 URL
                setDetailParamMap(code), // URL 파라미터
                accessToken, // API에서 발급받은 AT
                TR_ID_DETAIL, // TrId
                StockApiDto.DetailRp.class, // JSON 문자열 구조 그대로 매핑할 DTO Class
                StockApiDtoMapper::toDetail // 실제 사용자에게 반환할 DTO를 매핑하는 메소드
        );
    }


    // 주식 상세 정보를 조회하기 위한 파라미터
    private MultiValueMap<String, String> setDetailParamMap(String code) {

        return KisParamBuilder.builder()
                .marketCode("J")
                .stockCode(code)
                .build();
    }


    @Override
    public List<StockApiDto.Candle> getCandleList(String code, String accessToken, CandleType candleType) {

        return doGetStockRequest(
                URL_CHART, // 요청 URL
                setCandleParamMap(code, candleType), // URL 파라미터
                accessToken, // API에서 발급받은 AT
                TR_ID_CHART, // TrId
                StockApiDto.CandleRp.class, // JSON 문자열 구조 그대로 매핑할 DTO Class
                StockApiDtoMapper::toCandles // 실제 사용자에게 반환할 DTO를 매핑하는 메소드
        );
    }


    // 주식 상세 차트 정보를 조회하기 위한 파라미터
    private MultiValueMap<String, String> setCandleParamMap(String code, CandleType candleType) {

        return KisParamBuilder.builder()
                .stockCode(code)
                .marketCode("J")
                .periodType(candleType.getType())
                .priceAdjustType(CHART_PRICE_ADJUST_TYPE_ADJUSTED)
                .date1(calculateStartDate(candleType))
                .date2(TimeUtils.formatNowDateNoHyphen())
                .build();
    }


    private String calculateStartDate(CandleType candleType) {

        // [1] 현재 한국 시간 조회
        LocalDate startDate = TimeUtils.getNowLocalDate();

        // [2] 캔들 타입마다 시작 시간을 다르게 계산
        switch (candleType) {
            case DAY -> startDate = startDate.minusMonths(CHART_INTERVAL_DAY);
            case WEEK -> startDate = startDate.minusYears(CHART_INTERVAL_WEEK);
            case MONTH -> startDate = startDate.minusYears(CHART_INTERVAL_MONTH);
        };

        // [3] yyyyMMdd 형태로 변환 및 반환
        return TimeUtils.formatDateNoHyphen(startDate);
    }



    // F : 첫 번째로 변환하는 DTO Class, R : 반환하는 DTO Class
    private <F, R> R doGetStockRequest(
            String uri,
            MultiValueMap<String, String> params,
            String accessToken,
            String trId,
            Class<F> firstDtoClass,
            Function<F, R> mappingMethod) {

        // [1] 인증 요청 헤더
        String authorization = "Bearer %s".formatted(accessToken);

        // [2] API GET 요청 기본 spec 생성
        return kisClient.get()

                // [1] API 요청
                .uri(uriBuilder -> buildURI(uriBuilder, uri, params))
                .header("authorization", authorization)
                .header("tr_id", trId)
                .header("custtype", "P")
                .retrieve()

                // [2] 요청 결과 JSON 문자열로 변환
                .bodyToMono(String.class)
                .timeout(TIMEOUT_GET) // 요청 Timeout
                .retryWhen(setRetrySpec()) // 실패 시 재시도 로직
                .flatMap(ApiUtils::validateEmpty)

                // [3] JSON 응답 그대로 첫 번째 DTO 반환 (JSON 구조 그대로) -> 이후 필요한 데이터만 추출하여 최종 반환 DTO로 변환
                .map(json -> StrUtils.fromJson(json, firstDtoClass)) // JSON 형태 그대로 DTO로 변환
                .map(mappingMethod) // 최종 형태 DTO로 변환

                // [4] 예외 통합 처리
                .onErrorMap(Exception.class, throwError("KIS API 주식 정보 조회 실패"))

                // [5] 최종 결과 생성 (변환 결과)
                .block();
    }


    // 인증 요청 실패 시, 재시도를 위한 재시도 로직 spec 객체 설정
    private RetryBackoffSpec setRetrySpec() {

        return ApiRetry.builder() // Retry 설정
                .attempts(RETRY_MAX_ATTEMPTS)
                .minBackoff(RETRY_MIN_BACKOFF)
                .maxBackoff(RETRY_MAX_BACKOFF)
                .jitter(RETRY_JITTER)
                .loggerClass(this.getClass())
                .showBeforeRetryLog(false)
                .loggingMessage("KIS API 주식 정보 조회 시도")
                .apiFailedStatus(AppStatus.API_STOCK_REQUEST_FAILED)
                .build().toRetrySpec();
    }

    // API 요청 URL(URI) 생성
    private URI buildURI(UriBuilder uriBuilder, String uri, MultiValueMap<String, String> params) {
        return uriBuilder.path(uri).queryParams(params).build();
    }


    // 통합 실패 예외 처리
    private Function<Exception, Throwable> throwError(String message) {

        return ApiError.builder() // API 예외 처리
                .loggerClass(this.getClass())
                .message(message)
                .apiFailedStatus(AppStatus.API_STOCK_REQUEST_FAILED)
                .build().toErrorFunction();
    }



}
