package app.finup.layer.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 주식 종목 DTO 클래스
 * @author kcw
 * @since 2025-12-29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockAiDto {

    /**
     * 주식 상세 정보 AI 분석 결과를 담을 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetailAnalyzation implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String summary;
        private String investmentPoint;
        private String price;
        private String valuation;
        private String flow;
        private String risk;
    }


    /**
     * 차트 AI 분석 결과를 담을 DTO
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChartAnalyzation implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String recentTrend;
        private String comparisonWithPast;
        private String investorNote;
    }


    /**
     * AI 기반 종목 영상 추천 결과를 담을 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class YouTubeRecommendation implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

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
