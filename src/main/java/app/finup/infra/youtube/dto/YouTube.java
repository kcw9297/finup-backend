package app.finup.infra.youtube.dto;

import app.finup.common.constant.Const;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * YouTube API 로부터 얻어온 데이터를 담는 DTO 클래스
 * @author kcw
 * @since 2025-12-05
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YouTube {

    /**
     * 유튜브에서 받아온 영상 상세 데이터 응답 데이터를 담는 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideosRp {

        // 검색 결과 (key : items)
        private List<Item> items; // 길이가 1인 응답으로 제공

        @Getter
        static class Item {
            private String id; // videoId
            private Snippet snippet; // 영상 기본 정보
            private ContentDetails contentDetails; // 영상 상세 정보
            private Statistics statistics; // 영상 통계

            @Getter
            static class Snippet {
                private String title;
                private String channelTitle;
                private Thumbnails thumbnails;

                @Getter
                static class Thumbnails {
                    private Thumbnail high; // 400 * 360
                    private Thumbnail standard; // 640 * 480 (상세에만 존재)

                    @Getter
                    static class Thumbnail {
                        private String url; // 썸네일 url
                        private Integer width; // 썸네일 너비
                        private Integer height; // 섬네일 높이
                    }
                }
            }

            @Getter
            static class ContentDetails {

                @JsonFormat(pattern = Const.ISO8601)
                private Duration duration; // 영상 시간 (ISO8601 Duration 문자열 제공)
            }

            @Getter
            static class Statistics {
                private Long viewCount; // 조회수
                private Long likeCount; // 좋아요수
            }
        }
    }


    /**
     * 유튜브에서 받아온 영상 검색 데이터 응답을 담는 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchRp {

        // 검색 결과 (key : items)
        private List<Item> items; // 길이가 1인 응답으로 제공

        @Getter
        static class Item {
            private Id id; // videoId
            private Snippet snippet; // 영상 기본 정보

            @Getter
            static class Id {
                private String videoId;
            }

            @Getter
            static class Snippet {
                private String title;
                private String channelTitle;
                private LocalDateTime publishedAt;
                private Thumbnails thumbnails;

                @Getter
                static class Thumbnails {
                    private Thumbnail high; // 400 * 360

                    @Getter
                    static class Thumbnail {
                        private String url; // 썸네일 url
                        private Integer width; // 썸네일 너비
                        private Integer height; // 섬네일 높이
                    }
                }
            }
        }
    }



    /**
     * 유튜브 영상 목록 정보를 담는 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {

        // 검색은 snippet 정보만 보기 가능
        private String videoId; // 유튜브 영상 고유번호
        private String videoUrl; // 영상 URL
        private String thumbnailUrl; // high 섬네일 이미지 주소
        private String title; // 제목
    }


    /**
     * 유튜브 영상 상세 정보를 담는 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {

        // 입력 정보
        private String videoUrl;

        // 추출 정보 - snippet
        private String videoId; // 유튜브 영상 고유번호
        private String title; // 영상 제목 (최대 100자)
        private String channelTitle; // 영상을 업로드한 채널 이름
        private String thumbnailUrl; // high 섬네일 이미지 주소

        // 추출 정보 - contentDetails
        private Duration duration; // 영상 길이

        // 추출 정보 - statistics
        private Long viewCount; // 조회수
        private Long likeCount; // 좋아요 수
    }

}