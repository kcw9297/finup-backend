package app.finup.layer.domain.videolink.dto;


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
     * 링크 목록에 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {

        private String videoUrl;
        private Long ownerId; // 주인이 HOME인 경우 null
        private VideoLinkOwner videoLinkOwner;
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
        private String videoId;
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
        private Long ownerId; // 주인이 HOME인 경우 null
        private VideoLinkOwner videoLinkOwner;
        private Integer reorderPosition; // 0번째부터 표기

    }
}