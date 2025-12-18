package app.finup.layer.domain.videolink.dto;


import app.finup.layer.base.dto.SearchRequest;
import app.finup.layer.base.validation.annotation.YouTubeUrl;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 학습용 비디오 링크 DTO 클래스
 * @author kcw
 * @since 2025-12-02
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoLinkDto {

    /**
     * 리스트 조회 결과
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long videoLinkId;
        private String videoId;
        private String videoUrl;
        private String title;
        private String duration;
        private String description;
        private String thumbnailUrl;
        private String channelTitle;
        private LocalDateTime publishedAt;
        private Long viewCount;
        private Long likeCount;
        private String tags; // 태그 문자열
    }


    /**
     * 검색 요청
     */
    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class Search extends SearchRequest {

        private String filter = "";
        private String keyword = "";
        private String order = "latest";

        public Search() {
            super(6); // 한 줄에는 3개 씩 총 2줄
        }
    }


    /**
     * 링크 목록에 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {

        @YouTubeUrl
        private String videoUrl;
    }


    /**
     * 링크 정보 수정
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edit {

        private Long videoLinkId;

        @YouTubeUrl
        private String videoUrl;
    }


    /**
     * 영상 추천 요청을 담을 DTO (JSON 변환용)
     */
    @Getter
    @Builder
    public static class Recommendation {

        @JsonProperty("study")
        private StudyInfo study;

        @JsonProperty("candidates")
        private List<VideoCandidate> candidates;

        @JsonProperty("latestVideoLinkIds")
        private List<Long> latestVideoLinkIds;

        @Getter
        @Builder
        public static class StudyInfo {
            private String name;
            private String summary;
            private String detail;
            private int level;
        }

        @Getter
        @Builder
        public static class VideoCandidate {
            private Long videoLinkId;
            private String title;
            private String channelTitle;
            private String description;
            private String tags;
        }
    }
}