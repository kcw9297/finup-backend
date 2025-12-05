package app.finup.layer.domain.stocks.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Stocks api 데이터 -> DTO 매퍼 클래스
 * @author lky
 * @since 2025-12-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StocksDtoMapper {

    public static StocksDto.Detail toDetail(JsonNode output){
        return StocksDto.Detail.builder()
                /* 종목 기본 정보 */
                // 종목명 헤더
                .stckShrnIscd(output.path("stck_shrn_iscd").asText())
                .stckPrpr(output.path("stck_prpr").asText())
                .rprsMrktKorName(output.path("rprs_mrkt_kor_name").asText())

                // 종목 카드
                .bstpKorIsnm(output.path("bstp_kor_isnm").asText())
                .stckFcam(output.path("stck_fcam").asText())
                .htsAvls(output.path("hts_avls").asText())
                .lstnStcn(output.path("lstn_stcn").asText())

                /* 투자 지표 */
                // 가격
                .w52Hgpr(output.path("w52_hgpr").asText())
                .w52Lwpr(output.path("w52_lwpr").asText())
                .d250Hgpr(output.path("d250_hgpr").asText())
                .d250Lwpr(output.path("d250_lwpr").asText())

                // 가치평가
                .per(output.path("per").asText())
                .pbr(output.path("pbr").asText())
                .eps(output.path("eps").asText())
                .bps(output.path("bps").asText())

                // 수급 거래
                .frgnNtbyQty(output.path("frgn_ntby_qty").asText())
                .pgtrNtbyQty(output.path("pgtr_ntby_qty").asText())
                .htsFrgnEhrt(output.path("hts_frgn_ehrt").asText())
                .volTnrt(output.path("vol_tnrt").asText())

                // 리스크 상태
                .tempStopYn(output.path("temp_stop_yn").asText())
                .invtCafulYn(output.path("invt_caful_yn").asText())
                .shortOverYn(output.path("short_over_yn").asText())
                .mangIssuClsCode(output.path("mang_issu_cls_code").asText())

                .build();
    }
}
