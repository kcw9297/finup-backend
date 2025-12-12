package app.finup.layer.domain.stockChart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockChartDto {
    /**
     * KIS 차트 API 1건 응답 (가공 후)
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row{
        private List<Detail> output;
        private String rt_cd;
        private String msg_cd;
        private String msg1;
    }
    /**
     * 개별 캔들 + 우리가 계산한 이동평균/거래량평균
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detail {
        private String stck_bsop_date; //주식영업일자
        private String stck_oprc; //주식 시가2
        private String stck_hgpr; //주식 최고가
        private String stck_lwpr; //주식 최저가
        private String stck_clpr; //주식 종가
        private String acml_vol; //누적 거래량

        private Double ma5;
        private Double ma20;
        private Double ma60;

        private Double volumeMa5;
        private Double volumeMa20;
    }

    /**
     * AI 프롬프트로 넘길 입력 구조
     * - timeframe: DAY / WEEK / MONTH
     * - candles: 캔들 30개 정도
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AiInput {
        private String timeframe;          // DAY / WEEK / MONTH
        private List<CandleItem> candles;  // 캔들 리스트
    }

    /**
     * AI가 보기 좋은 형태로 정제된 캔들 데이터
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CandleItem {
        private String date;      // YYYYMMDD 등

        // 숫자는 가급적 wrapper 타입으로 (null 안전)
        private Double open;
        private Double high;
        private Double low;
        private Double close;
        private Long volume;

        private Double ma5;
        private Double ma20;
        private Double ma60;
        private Double volumeMa5;
        private Double volumeMa20;
    }

    /**
     * AI 분석 결과 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChartAi {
        private String trend;          // 상승 / 하락 / 횡보 + 설명
        private String volatility;     // 변동성 설명
        private String volumeAnalysis; // 거래량 해석
        private String summary;        // 초보자용 한 문단 설명

        private String timeframe;      // DAY / WEEK / MONTH
    }

    /**
     * 차트 + AI를 한 번에 내려주는 응답 DTO
     * - 컨트롤러에서 이걸 Api.ok()로 감싸서 리턴
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChartWithAi {
        private Row chart;     // KIS 차트 데이터
        private ChartAi ai;    // AI 분석 데이터
    }

}
