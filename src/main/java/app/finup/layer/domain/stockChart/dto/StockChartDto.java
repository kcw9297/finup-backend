package app.finup.layer.domain.stockChart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockChartDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row{
        private List<Detail> output;
        private String rt_cd;
        private String msg_cd;
        private String msg1;
    }
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

    //ai input용
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CandleAi{
        private String date;
        private int open;
        private int high;
        private int low;
        private int close;
        private long volume;
    }

    //ai output용
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChartAi {

        private String trend;          // 상승 / 하락 / 횡보
        private String volatility;     // 변동성 요약
        private String volumeAnalysis; // 거래량 해석
        private String summary;        // 초보자용 한 문단 설명

        private String timeframe;      // DAY / WEEK / MONTH
    }
}
