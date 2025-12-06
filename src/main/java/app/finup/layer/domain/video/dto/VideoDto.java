package app.finup.layer.domain.video.dto;


import lombok.*;

/**
 * 유튜브 API 조회 결과를 담는 DTO 클래스
 * @author kcw
 * @since 2025-12-07
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoDto {

    /**
     * YouTube API 상세 조회 결과
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {

        // DB에 저장된 정보
        private String videoId;
        private String videoUrl;
        private String title;
        private String duration;
        private String thumbnailUrl;
        private String channelTitle;
        private Long viewCount;
        private Long likeCount;
    }


    /**
     * YouTube API 검색 조회 결과 리스트
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {

        // 검색 걸과
        private String title;
        private String videoUrl;
        private String thumbnailUrl;
        private String channelTitle;
        private String comment; // AI 추천 이유 (30자 이내)
        private String recommendationLevel; // AI 추천도 (지금은 임시로 String)
    }

}