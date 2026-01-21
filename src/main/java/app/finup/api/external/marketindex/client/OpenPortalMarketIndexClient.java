package app.finup.api.external.marketindex.client;

import app.finup.api.external.marketindex.dto.MarketIndexApiDto;
import app.finup.api.external.marketindex.dto.MarketIndexApiDtoMapper;
import app.finup.api.utils.ApiUtils;
import app.finup.api.utils.ApiError;
import app.finup.api.utils.ApiRetry;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.TimeUtils;
import app.finup.common.utils.StrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenPortalMarketIndexClient implements MarketIndexClient {

    // 사용 의존성
    private final WebClient openPortalClient; // 금융위원회 지수 API Client

    // API 요청 관련 상수
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // 사용 상수
    @Value("${api.open-portal.key}")
    private String openPortalKey;


    // 주가지수 시세 조회 API 일괄 조회
    public List<MarketIndexApiDto.Row> getIndexList(LocalDate date) {

        return openPortalClient.get()

                // [1] API 요청 전송
                .uri(uri -> buildURL(uri, date))
                .retrieve()

                // [2] 요청 결과 JSON 문자열 변환
                .bodyToMono(String.class)
                .timeout(TIMEOUT) // timeout
                .retryWhen(ApiRetry.builder() // Retry 설정
                        .loggerClass(this.getClass())
                        .loggingMessage("OPEN PORTAL 시장 지수 목록 조회")
                        .apiFailedStatus(AppStatus.API_MARKET_INDEX_REQUEST_FAILED)
                        .build().toRetrySpec())
                .flatMap(ApiUtils::validateEmpty) // API 요청 결과 자체 검증

                // [3] API에서 얻은 모든 정보를 담은 DTO로 변환 후 결과 검증 (result 값 기반)
                .map(json -> StrUtils.fromJson(json, MarketIndexApiDto.IndexListRp.class)) // JSON 형태 그대로 DTO로 변환
                .flatMap(rp -> ApiUtils.validateCode( // 결과 코드 검증
                        rp, dto -> Objects.equals(dto.getResponse().getHeader().getResultCode(), "00")
                ))
                .flatMap(this::validateItem)

                // [4] 최종적으로 제공할 DTO로 변환 (필요 데이터만 추출)
                .map(MarketIndexApiDtoMapper::toRows)

                // [5] 예외 통합 처리
                .onErrorMap(Exception.class,
                        ApiError.builder() // API 예외 처리
                                .loggerClass(this.getClass())
                                .apiFailedStatus(AppStatus.API_MARKET_INDEX_REQUEST_FAILED)
                                .build().toErrorFunction()
                )
                .block();
    }


    /*
        영업일 13시 전에는 전날 데이터까지만 조회 가능
        휴일로 조회 시에는 결과가 반환되지 않으므로 평일 영업일 사이에만 조회
        searchdate : 검색 요청일
        data : 검색 요청 API 타입 (AP01: 환율, AP02: 대출금리, AP03: 국제금리)
     */

    private URI buildURL(UriBuilder uri, LocalDate date) {

        return uri
                .queryParam("serviceKey", openPortalKey)
                .queryParam("resultType", "json")
                .queryParam("numOfRows", 1000) // 모든 데이터를 받도록 큰 숫자
                //.queryParam("pageNo", 1)
                .queryParam("basDt", TimeUtils.formatDateNoHyphen(date))
                .build();
    }


    // 현재 DTO가 유효한지 검증
    private Mono<MarketIndexApiDto.IndexListRp> validateItem(MarketIndexApiDto.IndexListRp indexListRp) {

        return ApiUtils.validate(
                rp -> Objects.nonNull(rp) &&
                        Objects.nonNull(rp.getResponse()) &&
                        Objects.nonNull(rp.getResponse().getBody()) &&
                        Objects.nonNull(rp.getResponse().getBody().getItems()) &&
                        Objects.nonNull(rp.getResponse().getBody().getItems().getItem()) &&
                        !rp.getResponse().getBody().getItems().getItem().isEmpty(),
                indexListRp, AppStatus.API_RESPONSE_EMPTY
        );
    }

}