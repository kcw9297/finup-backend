package app.finup.layer.domain.videolink.dto;


import app.finup.layer.base.dto.SearchRequest;
import app.finup.layer.base.validation.annotation.YouTubeUrl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public final class VideoLinkAi {

    /**
     * AI 추천 결과
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Recommend implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private List<Item> items;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {

            private String videoId;
            private String videoUrl;
            private String title;
            private String duration;
            private String thumbnailUrl;
            private String channelTitle;
            private Long viewCount;
            private Long likeCount;
        }
    }


}