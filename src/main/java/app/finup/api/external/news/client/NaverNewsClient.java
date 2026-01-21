package app.finup.api.external.news.client;

import app.finup.api.external.news.enums.NewsSortType;
import app.finup.api.utils.ApiError;
import app.finup.api.utils.ApiUtils;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.StrUtils;
import app.finup.api.external.news.dto.NewsApi;
import app.finup.api.external.news.dto.NewsApiDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * NewsProvider 구현 클래스 (네이버 뉴스 제공)
 * @author kcw
 * @since 2025-12-24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NaverNewsClient implements NewsClient {

    // 사용 의존성
    private final WebClient naverClient;

    // 사용 상수
    private static final String PATH_SEARCH = "/v1/search/news.json";
    private static final Random RANDOM = new Random();

    // API 요청 관련 상수
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final int DELAY_BASE = 200;
    private static final int DELAY_MIN = 200;
    private static final int DELAY_MAX = 1000;


    @Override
    public List<NewsApi.Row> getLatest(String query, int amount, NewsSortType newsSortType) {

        try {
            // 일정 시간 대기 (jitter 외 추가 대기)
            delay();

            // 실제 API 호출
            return callApi(query, amount, newsSortType);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProviderException(AppStatus.API_NEWS_REQUEST_FAILED);

        }
    }


    // 스레드 랜덤시간 대기 수행
    private void delay() throws InterruptedException {
        int jitter = DELAY_MIN + RANDOM.nextInt(DELAY_MAX - DELAY_MIN + 1);
        int totalDelay = DELAY_BASE + jitter;
        Thread.sleep(totalDelay);
    }


    // API 호출
    private List<NewsApi.Row> callApi(String query, int amount, NewsSortType newsSortType) {

        // [1] API 요청
        List<NewsApi.Row> result = naverClient.get()
                .uri(uriBuilder -> buildSearchURI(uriBuilder, query, amount, newsSortType))
                .retrieve()
                .bodyToMono(String.class) // JSON 문자열로 변환
                .timeout(TIMEOUT) // API 요청 + 응답 역직렬화까지 걸리는 시간 Timeout
                .flatMap(ApiUtils::validateEmpty) // API 요청 결과 자체 검증
                .map(json -> StrUtils.fromJson(json, NewsApi.SearchRp.class)) // JSON 문자열 파싱
                .map(NewsApiDtoMapper::toRows) // 파싱한 DTO -> 정체한 DTO로 변환
                .onErrorMap(Exception.class,
                        ApiError.builder() // API 예외 처리
                                .loggerClass(this.getClass())
                                .apiFailedStatus(AppStatus.API_NEWS_REQUEST_FAILED)
                                .build().toErrorFunction()
                )
                .block();

        // [2] 결과를 정상 얻어온 경우, 작성일 역순으로 정렬 후 반환
        return Objects.isNull(result) ?
                null :
                result.stream()
                        .sorted(Comparator.comparing(NewsApi.Row::getPublishedAt).reversed()) // 최신순 정렬
                        .toList();
    }


    // URL builder - 검색에 필요한 파라미터 첨가
    private URI buildSearchURI(UriBuilder uriBuilder, String query, int amount, NewsSortType newsSortType) {

        return uriBuilder
                // 기본 설정 (건들면 안 되는 옵션)
                .path(PATH_SEARCH) // 유튜브 영상 상세 API
                .queryParam("query", query) // 검색어
                .queryParam("sort", newsSortType.getType()) // 정렬 ("sim": 관련도순, "date": 최신순)
                .queryParam("display", amount) // 최대 표시 개수 (최대 50개까지 가능)
                .build();
    }

}
