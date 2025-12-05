package app.finup.infra.youtube.provider;

import app.finup.common.constant.Const;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import app.finup.infra.youtube.dto.YouTube;
import app.finup.infra.youtube.dto.YouTubeDtoMapper;
import app.finup.infra.youtube.utils.YouTubeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * YouTube Data API v3 기반 YoutubeProvider 구현 클래스
 * @author kcw
 * @since 2025-12-05
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class V3YouTubeProvider implements YouTubeProvider {

    private final WebClient youTubeClient;

    @Value("${api.youtube.key}")
    private String key;


    @Cacheable(
            value = Const.PREFIX_KEY_YOUTUBE_DETAIL,
            key = "#videoId" // videoId 단위로 캐싱
    )
    @Override
    public YouTube.Detail getVideo(String videoId) {

        LogUtils.showWarn(this.getClass(), "호출 완료");

        return youTubeClient.get()

                // [1] URI 생성
                .uri(uriBuilder -> buildVideoURI(uriBuilder, videoId))

                // [2] HTTP 요청 수행
                .retrieve()

                // [3] 4xx, 5xx 예외 처리
                .onStatus(HttpStatusCode::is4xxClientError, this::throwVideo4xx)
                .onStatus(HttpStatusCode::is5xxServerError, this::throwVideo5xx)

                // [4] JSON 역직렬화
                .bodyToMono(YouTube.VideosRp.class)

                // [5] 빈 응답 처리 (응답 자체가 없는 경우)
                .switchIfEmpty(Mono.error(new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED)))

                // [6] 성공 시, 데이터 검증 (데이터가 비어있지 않은지 확인)
                .filter(response -> Objects.nonNull(response) && !response.getItems().isEmpty())
                .switchIfEmpty(Mono.error(new ProviderException(AppStatus.YOUTUBE_URL_NOT_VALID)))
                .map(rp -> YouTubeDtoMapper.toDetail(rp, YouTubeUtils.getVideoUrl(videoId))) // 최종 차리

                // [7] 타임아웃 설정
                .timeout(Duration.ofSeconds(5))

                // [8] 예외 통합 처리 (가장 마지막에 작성해야 함)
                .onErrorMap(Exception.class, ex -> {
                    LogUtils.showError(this.getClass(), "Youtube 영상 상세 API 요청 실패.\n원인 : %s", ex.getMessage());
                    if (ex instanceof ProviderException) return ex;
                    return new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED, ex);
                })
                .block();
    }

    // 상세 정보 요청 생성
    private URI buildVideoURI(UriBuilder uriBuilder, String videoId) {
        return uriBuilder
                .path("/videos") // 유튜브 영상 상세 API
                .queryParam("key", key) // API KEY
                .queryParam("id", videoId)
                .queryParam("part", List.of("snippet", "contentDetails", "statistics"))
                .build();
    }

    // 상세 정보 요청 4xx 처리
    private Mono<Throwable> throwVideo5xx(ClientResponse response) {
        return Mono.error(new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED));
    }

    // 상세 정보 요청 5xx 처리
    private Mono<Throwable> throwVideo4xx(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    LogUtils.showWarn(this.getClass(), "YouTube 영상 상세 조회 실패. 원인 : %s", body);
                    return Mono.error(new ProviderException(AppStatus.YOUTUBE_URL_NOT_VALID));
                });
    }


    @Override
    public List<YouTube.Row> searchVideo(String q) {
        return List.of();
    }

}
