package app.finup.layer.domain.stock.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Stocks api 데이터 -> DTO 매퍼 클래스
 * @author lky
 * @since 2025-12-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockDtoMapper {

    public static StockDto.MarketCapRow toMarketCapRow(JsonNode output) {
        return StockDto.MarketCapRow.builder()
                .mkscShrnIscd(output.path("mksc_shrn_iscd").asText())
                .dataRank(output.path("data_rank").asText())
                .htsKorIsnm(output.path("hts_kor_isnm").asText())
                .stckPrpr(output.path("stck_prpr").asText())
                .prdyVrss(output.path("prdy_vrss").asText())
                .prdyVrssSign(output.path("prdy_vrss_sign").asText())
                .prdyCtrt(output.path("prdy_ctrt").asText())
                .stckAvls(output.path("stck_avls").asText())
                .mrktWholAvlsRlim(output.path("mrkt_whol_avls_rlim").asText())
                .build();
    }

    public static StockDto.Detail toDetail(String htsKorIsnm, JsonNode output){
        return StockDto.Detail.builder()
                /* 종목 기본 정보 */
                // 종목명 헤더
                .htsKorIsnm(htsKorIsnm)
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

    public static StockDto.YoutubeVideo toYoutube(String keyword, StockDto.YoutubeSearchResponse response) {

        StockDto.YoutubeSearchResponse.Item item = response.getItems().get(0);

        return StockDto.YoutubeVideo.builder()
                .keyword(keyword)
                .videoId(item.getId().getVideoId())
                .title(StringEscapeUtils.unescapeHtml4(item.getSnippet().getTitle()))
                .channelTitle(StringEscapeUtils.unescapeHtml4(item.getSnippet().getChannelTitle()))
                .thumbnailUrl(item.getSnippet().getThumbnails().getHigh().getUrl())
                .build();
    }
}
