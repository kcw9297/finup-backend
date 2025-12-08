package app.finup.infra.youtube.provider;

import app.finup.common.constant.Const;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.infra.youtube.dto.YouTube;
import app.finup.infra.youtube.dto.YouTubeDtoMapper;
import app.finup.infra.youtube.utils.YouTubeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
            value = "YT",
            key = "#videoId"
    )
    @Override
    public YouTube.Detail getVideo(String videoId) {

        LogUtils.showWarn(this.getClass(), "호출 완료");

        // [1] API 요청
        return youTubeClient.get()

                // [1] URI 생성
                .uri(uriBuilder -> buildVideoURI(uriBuilder, List.of(videoId)))

                // [2] HTTP 요청 수행
                .header("Content-Type", "application/json")
                .retrieve()

                // [3] JSON 역직렬화
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5)) // API 요청 + 응답 역직렬화까지 Timeout 시간

                // [4] 빈 응답 처리 (응답 자체가 없는 경우)
                .switchIfEmpty(Mono.error(new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED)))

                // [5] 역직렬화 수행
                .map(json -> StrUtils.fromJson(json, YouTube.VideosRp.class))

                // [6] 만약 비어있는 경우 예외 반환
                .filter(rp -> !rp.getItems().isEmpty())
                .switchIfEmpty(Mono.error(new ProviderException(AppStatus.YOUTUBE_URL_NOT_VALID)))

                // [7] 최종적으로 외부에 제공할 DTO 변환
                .map(rp -> YouTubeDtoMapper.toDetail(rp, videoId))

                // [8] 예외 통합 처리 (가장 마지막에 작성해야 함)
                .onErrorMap(Exception.class, ex -> {
                    LogUtils.showError(this.getClass(), "Youtube 영상 상세 API 요청 실패.\n원인 : %s", ex.getMessage());
                    if (ex instanceof ProviderException) return ex;
                    return new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED, ex);
                })
                .block();
    }

    @Cacheable(
            value = "youtubeVideos",
            key = "T(java.util.stream.Collectors).joining(',', #videoIds.stream().sorted().toArray())"
    )
    @Override
    public List<YouTube.Detail> getVideos(List<String> videoIds) {

        return youTubeClient.get()

                // [1] URI 생성
                .uri(uriBuilder -> buildVideoURI(uriBuilder, videoIds))

                // [2] HTTP 요청 수행
                .header("Content-Type", "application/json")
                .retrieve()

                // [3] JSON 역직렬화
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5)) // API 요청 + 응답 역직렬화까지 Timeout 시간

                // [4] 빈 응답 처리 (응답 자체가 없는 경우)
                .switchIfEmpty(Mono.error(new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED)))

                // [5] 역직렬화 수행
                .map(json -> StrUtils.fromJson(json, YouTube.VideosRp.class))

                // [6] 최종적으로 외부에 제공할 DTO 변환 (비어 있는 경우에 대한 예외는 처리하지 않음)
                .map(rp -> YouTubeDtoMapper.toDetails(rp, videoIds))

                // [7] 예외 통합 처리 (가장 마지막에 작성해야 함)
                .onErrorMap(Exception.class, ex -> {
                    LogUtils.showError(this.getClass(), "Youtube 영상 상세 API 요청 실패.\n원인 : %s", ex.getMessage());
                    if (ex instanceof ProviderException) return ex;
                    return new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED, ex);
                })
                .block();
    }


    // 상세 정보 요청 생성 (건들지 말 것)
    private URI buildVideoURI(UriBuilder uriBuilder, List<String> videoIds) {
        return uriBuilder
                .path("/videos") // 유튜브 영상 상세 API
                .queryParam("key", key) // API KEY
                .queryParam("id", videoIds)
                .queryParam("part", List.of("snippet", "contentDetails", "statistics"))
                .build();
    }


    @Override
    public List<YouTube.Row> searchVideo(String q) {

        LogUtils.showWarn(this.getClass(), "호출 완료");

        return youTubeClient.get()

                // [1] URI 생성
                .uri(uriBuilder -> buildSearchURI(uriBuilder, q))

                // [2] HTTP 요청 수행
                .retrieve()

                // [3] JSON 역직렬화
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5)) // API 요청 + 응답 역직렬화까지 Timeout 시간

                // [4] 빈 응답 처리 (응답 자체가 없는 경우)
                .switchIfEmpty(Mono.error(new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED)))

                // [5] 성공 시, 역직렬화 수행 및 DTO 변환 (검색 결과는 비어있을 수 있음)
                .map(json -> StrUtils.fromJson(json, YouTube.SearchRp.class))
                .map(YouTubeDtoMapper::toRows)

                // [6] 예외 통합 처리 (가장 마지막에 작성해야 함)
                .onErrorMap(Exception.class, ex -> {
                    LogUtils.showError(this.getClass(), "Youtube 영상 상세 API 요청 실패.\n원인 : %s", ex.getMessage());
                    if (ex instanceof ProviderException) return ex;
                    return new ProviderException(AppStatus.YOUTUBE_REQUEST_FAILED, ex);
                })
                .block();
    }

    /*
        검색 정보 요청 생성
        조절 가능한 옵션은 아래와 같음 (그 외 옵션은 건들지 말 것)
        [order]
            - relevance 관련성 순 (기본값)
            - viewCount 조회수 순 - 인기있는 영상
            - rating 평점 순
            - date 최신순
        [videoDuration]
            - medium : 4~20분 사이 영상
            - long : 20분 이상 영상
            - short : 4분 미만 영상
     */

    private URI buildSearchURI(UriBuilder uriBuilder, String q) {

        return uriBuilder
                // 기본 설정 (건들면 안 되는 옵션)
                .path("/search") // 유튜브 영상 상세 API
                .queryParam("key", key) // API KEY
                .queryParam("type", "video") // 영상만 가져옴
                .queryParam("maxResults", 50) // 최대 검색 영상 개수 (최대 50)
                .queryParam("part", "snippet") // snippet 정보만 조회 가능
                .queryParam("relevanceLanguage", "ko") // 한국 영상
                .queryParam("publishedAfter", YouTubeUtils.toYouTubeDateTime(LocalDateTime.now().minusYears(1))) // 최근 1년 내
                .queryParam("videoDefinition", "high") // HD 영상만

                // 조작 가능한 설정 (정렬, 검색할 영상 길이)
                .queryParam("order", "viewCount") // 정렬 기준 (상단 주석 참고)
                .queryParam("videoDuration", "long") // 20분 이상 긴 영상만 (보통 유익한 분석영상이 많음)

                // 영상 검색어 (AI 혹은 사용자가 조작해야 함)
                .queryParam("q", q)
                .build();
    }

}
