package app.finup.layer.domain.news.dto;

import app.finup.layer.domain.news.support.NewsObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 뉴스 AI 분석 결과를 담기 위한 DTO
 * @author kcw
 * @since 2026-02-04
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsAiDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AnalysisWords implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
        private String meaning;
    }
}
