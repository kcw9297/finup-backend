package app.finup.layer.domain.video.dto;


import app.finup.layer.domain.videolink.enums.VideoLinkOwner;
import lombok.*;

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
    public static class Row {

        // DB에 저장된 정보
        private Long videoLinkId;
        private String videoId;
        private String videoUrl;
        private Double displayOrder;

        // YouTube API로 조회되는 정보
        private String title;
        private String duration;
        private String thumbnailUrl;
        private String channelTitle;
        private Long viewCount;
        private Long likeCount;
    }


    /**
     * YouTube API 조회 결과
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FetchDetail {

        private String title;
        private String videoUrl;
        private String duration;
        private String thumbnailUrl;
        private String channelTitle;
        private Long viewCount;
        private Long likeCount;
    }


    /**
     * 링크 목록에 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {

        // 프론트에서 얻어오는 정보
        private Long ownerId; // 주인이 HOME인 경우 null
        private VideoLinkOwner videoLinkOwner;
        private String videoUrl;
        private Long lastVideoLinkId; // 가장 마지막에 있던 영상링크 번호

        // 하단 정보는 유튜브 API에서 제공 받은 후 채우는 정보
        private String videoId;
        private String thumbnailUrl;
        private String title;

        /**
         * API에서 얻어온 정보 추가
         * @param videoId 영상 고유번호
         * @param thumbnailUrl 썸네일 이미지 주소
         * @param title 영상 제목
         */
        public void setApiInfo(String videoId, String thumbnailUrl, String title) {
            this.videoId = videoId;
            this.thumbnailUrl = thumbnailUrl;
            this.title = title;
        }

    }


    /**
     * 링크 정보 수정
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edit {

        // 프론트에서 얻어오는 정보
        private Long videoLinkId;
        private String videoUrl;

        // 하단 정보는 유튜브 API에서 제공 받은 후 채우는 정보
        private String videoId;
        private String thumbnailUrl;
        private String title;

        /**
         * API에서 얻어온 정보 추가
         * @param videoId 영상 고유번호
         * @param thumbnailUrl 썸네일 이미지 주소
         * @param title 영상 제목
         */
        public void setApiInfo(String videoId, String thumbnailUrl, String title) {
            this.videoId = videoId;
            this.thumbnailUrl = thumbnailUrl;
            this.title = title;
        }
    }


    /**
     * 링크 정보 수정
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reorder {

        private Long videoLinkId;
        private Long beforeVideoLinkId;
        private Long afterVideoLinkId;
    }
}