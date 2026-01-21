package app.finup.api.external.financialindex.client;

import app.finup.api.utils.ApiUtils;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.TimeUtils;
import app.finup.common.utils.StrUtils;
import app.finup.api.utils.ApiError;
import app.finup.api.utils.ApiRetry;
import app.finup.api.external.financialindex.dto.FinancialIndexApiDto;
import app.finup.api.external.financialindex.dto.FinancialIndexApiDtoMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

/**
 * KEXIM(한국수출입은행) API 접든 IndicatorClient 구현체
 * @author kcw
 * @since 2026-01-14
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class KeximFinancialIndexClient implements FinancialIndexClient {

    // 사용 의존성
    private final WebClient keximClient; // 한국수출입은행 API Client

    // API 요청 관련 상수
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final Double RETRY_JITTER = 0.75;

    @Value("${api.kexim.key}")
    private String keximKey;


    @Override
    public List<FinancialIndexApiDto.ExchangeRateRow> getExchangeRates(LocalDate date) {

        return keximClient.get()

                // [1] API 요청 전송
                .uri(uri -> buildURL(date, uri))
                .retrieve()

                // [2] 요청 결과 JSON 문자열 변환
                .bodyToMono(String.class)
                .timeout(TIMEOUT) // timeout
                .retryWhen(ApiRetry.builder() // Retry 설정
                        .jitter(RETRY_JITTER)
                        .loggerClass(this.getClass())
                        .loggingMessage("KEXIM 환율 데이터 목록 조회")
                        .apiFailedStatus(AppStatus.API_INDICATOR_REQUEST_FAILED)
                        .build().toRetrySpec())
                .flatMap(ApiUtils::validateEmpty) // API 요청 결과 검증

                // [3] API에서 얻은 모든 정보를 담은 DTO로 변환 후 결과 검증 (result 값 기반)
                .map(json -> StrUtils.fromJson(json, new TypeReference<List<FinancialIndexApiDto.ExchangeRateRp>>() {})) // JSON 형태 그대로 DTO로 변환
                .flatMap(rpList -> ApiUtils.validateAllCode(rpList, rp -> rp.getResult() == 1)) // 결과 코드 검증

                // [4] 최종적으로 제공할 DTO로 변환 (필요 데이터만 추출)
                .map(FinancialIndexApiDtoMapper::toExchangeRateRows)

                // [5] 예외 통합 처리
                .onErrorMap(Exception.class,
                        ApiError.builder() // API 예외 처리
                                .loggerClass(this.getClass())
                                .apiFailedStatus(AppStatus.API_INDICATOR_REQUEST_FAILED)
                                .build().toErrorFunction()
                )
                .block();
    }


    /*
        영업일 11시 전에는 전날 데이터까지만 조회 가능
        휴일로 조회 시에는 결과가 반환되지 않으므로 평일 영업일 사이에만 조회
        searchdate : 검색 요청일
        data : 검색 요청 API 타입 (AP01: 환율, AP02: 대출금리, AP03: 국제금리)
     */

    private URI buildURL(LocalDate date, UriBuilder uri) {
        return uri
                .queryParam("authkey", keximKey)
                .queryParam("searchdate", TimeUtils.formatDate(date))
                .queryParam("data", "AP01")
                .build();
    }


}