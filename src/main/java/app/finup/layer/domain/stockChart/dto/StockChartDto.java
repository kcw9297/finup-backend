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
}
