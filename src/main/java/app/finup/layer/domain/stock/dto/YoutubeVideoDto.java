package app.finup.layer.domain.stock.dto;


import lombok.*;

import java.util.List;

/**
 * 유튜브 DTO 클래스
 * @author lky
 * @since 2025-12-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class YoutubeVideoDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class YoutubeVideo {

        private String videoId;       // 비디오ID
        private String title;         // 제목
        private String channelTitle;  // 채널명
        private String thumbnailUrl;  // 썸네일

    }

    // 2️⃣ API 원본 JSON 매핑 DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YoutubeSearchResponse {
        private List<Item> items;

        @Data
        public static class Item {
            private Id id;
            private Snippet snippet;
        }

        @Data
        public static class Id {
            private String videoId;
        }

        @Data
        public static class Snippet {
            private String title;
            private String channelTitle;
            private Thumbnails thumbnails;
            private String description;
        }

        @Data
        public static class Thumbnails {
            private High high;
        }

        @Data
        public static class High {
            private String url;
        }
    }
}