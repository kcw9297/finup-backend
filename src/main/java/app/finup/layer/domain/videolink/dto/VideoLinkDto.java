package app.finup.layer.domain.videolink.dto;


import app.finup.layer.base.dto.SearchRequest;
import lombok.*;

import java.time.LocalDateTime;

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

        private Long videoLinkId;
        private String videoId;
        private String videoUrl;
        private String title;
        private String duration;
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
    }


    /**
     * 링크 목록에 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {

        // 사용자 입력 데이터 @YoutubeUrl 검증 애노테이션 추가 예정
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
        private String videoUrl;
    }

}