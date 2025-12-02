package app.finup.layer.domain.videolink.dto;

import app.finup.layer.domain.study.dto.StudyDto;
import app.finup.layer.domain.videolink.enums.VideoLinkOwner;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 학습용 비디오 링크 DTO 클래스
 * @author kcw
 * @since 2025-12-02
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoLinkDto {

    /**
     * 리스트 결과로 사용
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Summary {

        private Long videoLinkId;
        private Long videoId;
        private String videoUrl;
        private String thumbnailUrl;
        private String title;
        private Double displayOrder;
    }
}