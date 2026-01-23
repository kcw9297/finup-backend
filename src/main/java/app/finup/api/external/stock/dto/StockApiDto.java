package app.finup.api.external.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Duration;
import java.util.List;


/**
 * 주식 종목 정보를 불러오는 API 정보를 담는 DTO 클레스
 * @author kcw
 * @since 2025-12-25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StockApiDto {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IssueRp {

        @JsonProperty("access_token")
        private String accessToken; //접근토큰

        @JsonProperty("access_token_token_expired")
        private String accessTokenExpired; // 토큰 만료 시간

        @JsonProperty("token_type")
        private String tokenType;   //접근토큰유형

        @JsonProperty("expires_in")
        private int expiresIn;      //접근토큰 유효기간
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Issue {

        private String accessToken;
        private Duration ttl;
    }


    /**
     * 주식 조회 목록 결과를 저장하기 위한 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MarketCapListRp {

        @JsonProperty("output")
        private List<Row> rows;

        @JsonProperty("rt_cd")
        private String rtCd;  // 응답 코드

        @JsonProperty("msg_cd")
        private String msgCd;  // 메시지 코드

        @JsonProperty("msg1")
        private String msg1;  // 메시지

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Row {

            @JsonProperty("mksc_shrn_iscd")
            private String stockCode; // 종목코드

            @JsonProperty("hts_kor_isnm")
            private String stockName; // 종목명

            @JsonProperty("data_rank")
            private Integer rank; // 순위

            @JsonProperty("stck_prpr")
            private Long currentPrice; // 현재가

            @JsonProperty("prdy_vrss")
            private Long priceChange; // 전일대비 변동가

            @JsonProperty("prdy_vrss_sign")
            private Integer priceChangeSign; // 전일대비 부호 (1: 상한, 2: 상승, 3: 보합, 4: 하한, 5: 하락)

            @JsonProperty("prdy_ctrt")
            private Double changeRate; // 전일대비율 (%)

            @JsonProperty("acml_vol")
            private Long accumulatedVolume; // 누적 거래량 (거래된 주식 수량)

            @JsonProperty("lstn_stcn")
            private Long listedShares; // 상장 주식수

            @JsonProperty("stck_avls")
            private Long marketCap; // 시가총액 (단위: 억원)

            @JsonProperty("mrkt_whol_avls_rlim")
            private Double marketCapRatio; // 전체 시가총액 대비 비중 (%)
        }
    }

    /**
     * 종목 시가총액 순위 리스트 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketCapRow {

        private String stockCode; // mksc_shrn_iscd
        private Integer rank; // data_rank
        private String stockName; // hts_kor_isnm
        private Long currentPrice; // stck_prpr
        private Long priceChange; // prdy_vrss
        private Integer priceChangeSign; // prdy_vrss_sign
        private Double changeRate; // prdy_ctrt
        private Long marketCap; // stck_avls
        private Double marketCapRatio; // mrkt_whol_avls_rlim
    }


    /**
     * 주식 조회 목록 결과를 저장하기 위한 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TradingValueListRp {

        @JsonProperty("output")
        private List<Row> rows;

        @JsonProperty("rt_cd")
        private String rtCd;  // 응답 코드

        @JsonProperty("msg_cd")
        private String msgCd;  // 메시지 코드

        @JsonProperty("msg1")
        private String msg1;  // 메시지

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Row {

            @JsonProperty("mksc_shrn_iscd")
            private String stockCode; // 종목코드

            @JsonProperty("hts_kor_isnm")
            private String stockName; // 종목명

            @JsonProperty("data_rank")
            private Integer rank; // 순위

            @JsonProperty("stck_prpr")
            private Long currentPrice; // 현재가

            @JsonProperty("prdy_vrss_sign")
            private Integer priceChangeSign; // 전일대비 부호 (1: 상한, 2: 상승, 3: 보합, 4: 하한, 5: 하락)

            @JsonProperty("prdy_vrss")
            private Long priceChange; // 전일대비 변동가

            @JsonProperty("prdy_ctrt")
            private Double changeRate; // 전일대비율 (%)

            @JsonProperty("acml_vol")
            private Long accumulatedVolume; // 누적 거래량

            @JsonProperty("prdy_vol")
            private Long previousVolume; // 전일 거래량

            @JsonProperty("lstn_stcn")
            private Long listedShares; // 상장 주식수

            @JsonProperty("avrg_vol")
            private Long averageVolume; // 평균 거래량

            @JsonProperty("n_befr_clpr_vrss_prpr_rate")
            private Double nDayChangeRate;   // N일전 종가 대비 현재가 변동률 (N = 1)

            @JsonProperty("vol_inrt")
            private Double volumeIncreaseRate; // 거래량 증가율 (%)

            @JsonProperty("vol_tnrt")
            private Double volumeTurnoverRate; // 거래량 회전율 (%)

            @JsonProperty("nday_vol_tnrt")
            private Double nDayVolumeTurnoverRate; // N일 거래량 회전율 (%)

            @JsonProperty("avrg_tr_pbmn")
            private Long averageTradingValue; // 평균 거래대금

            @JsonProperty("tr_pbmn_tnrt")
            private Double tradingValueTurnoverRate; // 거래대금 회전율 (%)

            @JsonProperty("nday_tr_pbmn_tnrt")
            private Double nDayTradingValueTurnoverRate; // N일 거래대금 회전율 (%) (N = 1)

            @JsonProperty("acml_tr_pbmn")
            private Long accumulatedTradingValue;  // 누적 거래대금
        }
    }

    /**
     * 종목 거래대금 순위 리스트 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TradingValueRow {

        private String stockName; // hts_kor_isnm
        private String stockCode; // mksc_shrn_iscd
        private Integer rank; // data_rank
        private Long currentPrice; // stck_prpr
        private Integer priceChangeSign; // prdy_vrss_sign
        private Long priceChange; // prdy_vrss
        private Double changeRate; // prdy_ctrt
        private Long accumulatedTradingValue; // acml_tr_pbmn
        private Long averageVolume; // avrg_vol
        private Long accumulatedVolume; // acml_vol
    }



    /**
     * 주식 상세 정보 조회 결과를 담기 위한 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetailRp {

        @JsonProperty("output")
        private Detail detail;

        @JsonProperty("rt_cd")
        private String rtCd;  // 응답 코드

        @JsonProperty("msg_cd")
        private String msgCd;  // 메시지 코드

        @JsonProperty("msg1")
        private String msg1;  // 메시지

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Detail {

            // 기본 정보
            @JsonProperty("stck_shrn_iscd")
            private String stockCode;  // 종목코드 (6자리)

            @JsonProperty("bstp_kor_isnm")
            private String sectorName; // 업종명

            @JsonProperty("rprs_mrkt_kor_name")
            private String marketIndexName;  // 대표 시장 지수명 (KOSPI200 등)

            // 가격 정보 (목록 API와 동일)
            @JsonProperty("stck_prpr")
            private Long currentPrice; // 현재가

            @JsonProperty("prdy_vrss")
            private Long priceChange; // 전일대비 변동가

            @JsonProperty("prdy_vrss_sign")
            private Integer priceChangeSign; // 전일대비 부호

            @JsonProperty("prdy_ctrt")
            private Double changeRate;  // 전일대비율 (%)

            @JsonProperty("stck_oprc")
            private Long openPrice; // 시가

            @JsonProperty("stck_hgpr")
            private Long highPrice;  // 고가

            @JsonProperty("stck_lwpr")
            private Long lowPrice;  // 저가

            @JsonProperty("stck_sdpr")
            private Long basePrice; // 기준가 (전일 종가)

            @JsonProperty("stck_mxpr")
            private Long upperLimit; // 상한가

            @JsonProperty("stck_llam")
            private Long lowerLimit; // 하한가

            @JsonProperty("wghn_avrg_stck_prc")
            private Double weightedAveragePrice; // 가중평균가

            // 거래 정보 (목록 API와 동일)
            @JsonProperty("acml_vol")
            private Long tradingVolume; // 누적 거래량

            @JsonProperty("acml_tr_pbmn")
            private Long accumulatedTradingValue; // 누적 거래대금

            @JsonProperty("vol_tnrt")
            private Double volumeTurnoverRate; // 거래량 회전율 (%)

            @JsonProperty("prdy_vrss_vol_rate")
            private Double volumeChangeRate; // 전일 대비 거래량 비율 (%)

            // 시가총액 및 주식 정보 (목록 API와 동일)
            @JsonProperty("lstn_stcn")
            private Long listedShares; // 상장 주식수

            @JsonProperty("hts_avls")
            private Long marketCap; // 시가총액 (단위: 억원)

            @JsonProperty("per")
            private Double per; // PER (주가수익비율)

            @JsonProperty("pbr")
            private Double pbr; // PBR (주가순자산비율)

            @JsonProperty("eps")
            private Double eps; // EPS (주당순이익)

            @JsonProperty("bps")
            private Double bps; // BPS (주당순자산가치)

            // 외국인/기관 정보
            @JsonProperty("hts_frgn_ehrt")
            private Double foreignOwnershipRate; // 외국인 보유율 (%)

            @JsonProperty("frgn_ntby_qty")
            private Long foreignNetBuyQty; // 외국인 순매수량

            @JsonProperty("frgn_hldn_qty")
            private Long foreignHoldingQty; // 외국인 보유량

            @JsonProperty("pgtr_ntby_qty")
            private Long programNetBuyQty; // 프로그램 순매수량

            // 호가 관련
            @JsonProperty("dmrs_val")
            private Long askingPrice; // 매도호가

            @JsonProperty("dmsp_val")
            private Long biddingPrice;  // 매수호가

            @JsonProperty("pvt_scnd_dmrs_prc")
            private Long secondAskingPrice; // 2차 매도호가

            @JsonProperty("pvt_frst_dmrs_prc")
            private Long firstAskingPrice; // 1차 매도호가

            @JsonProperty("pvt_pont_val")
            private Long pivotPrice; // 피봇 가격

            @JsonProperty("pvt_frst_dmsp_prc")
            private Long firstBiddingPrice; // 1차 매수호가

            @JsonProperty("pvt_scnd_dmsp_prc")
            private Long secondBiddingPrice; // 2차 매수호가

            // 52주 최고/최저
            @JsonProperty("w52_hgpr")
            private Long week52High; // 52주 최고가

            @JsonProperty("w52_hgpr_date")
            private String week52HighDate; // 52주 최고가 일자

            @JsonProperty("w52_hgpr_vrss_prpr_ctrt")
            private Double week52HighChangeRate; // 52주 최고가 대비 현재가 변동률

            @JsonProperty("w52_lwpr")
            private Long week52Low; // 52주 최저가

            @JsonProperty("w52_lwpr_date")
            private String week52LowDate; // 52주 최저가 일자

            @JsonProperty("w52_lwpr_vrss_prpr_ctrt")
            private Double week52LowChangeRate; // 52주 최저가 대비 현재가 변동률

            // 연중 최고/최저
            @JsonProperty("stck_dryy_hgpr")
            private Long yearHigh; // 연중 최고가

            @JsonProperty("dryy_hgpr_date")
            private String yearHighDate; // 연중 최고가 일자

            @JsonProperty("dryy_hgpr_vrss_prpr_rate")
            private Double yearHighChangeRate;  // 연중 최고가 대비 변동률

            @JsonProperty("stck_dryy_lwpr")
            private Long yearLow; // 연중 최저가

            @JsonProperty("dryy_lwpr_date")
            private String yearLowDate; // 연중 최저가 일자

            @JsonProperty("dryy_lwpr_vrss_prpr_rate")
            private Double yearLowChangeRate; // 연중 최저가 대비 변동률

            // 250일 최고/최저
            @JsonProperty("d250_hgpr")
            private Long days250High; // 250일 최고가

            @JsonProperty("d250_hgpr_date")
            private String days250HighDate; // 250일 최고가 일자

            @JsonProperty("d250_hgpr_vrss_prpr_rate")
            private Double days250HighChangeRate; // 250일 최고가 대비 변동률

            @JsonProperty("d250_lwpr")
            private Long days250Low; // 250일 최저가

            @JsonProperty("d250_lwpr_date")
            private String days250LowDate;  // 250일 최저가 일자

            @JsonProperty("d250_lwpr_vrss_prpr_rate")
            private Double days250LowChangeRate;  // 250일 최저가 대비 변동률

            // 거래 상태
            @JsonProperty("iscd_stat_cls_code")
            private String stockStatusCode; // 종목 상태 (55: 신용가능)

            @JsonProperty("marg_rate")
            private Double marginRate; // 증거금 비율 (%)

            @JsonProperty("crdt_able_yn")
            private String creditAvailable; // 신용 가능 여부 (Y/N)

            @JsonProperty("grmn_rate_cls_code")
            private String marginRateCode;  // 증거금률 구분 코드

            @JsonProperty("temp_stop_yn")
            private String tempStopYn; // 임시 정지 여부 (Y/N)

            @JsonProperty("oprc_rang_cont_yn")
            private String openPriceRangeContinuityYn; // 시가 범위 연장 여부

            @JsonProperty("clpr_rang_cont_yn")
            private String closePriceRangeContinuityYn; // 종가 범위 연장 여부

            @JsonProperty("vi_cls_code")
            private String viCode; // VI(변동성완화장치) 발동 여부 (N: 미발동)

            @JsonProperty("ovtm_vi_cls_code")
            private String overtimeViCode; // 시간외 VI 발동 여부

            @JsonProperty("ssts_yn")
            private String shortSellingAvailable;  // 공매도 가능 여부 (Y/N)

            @JsonProperty("invt_caful_yn")
            private String investmentCautionYn; // 투자주의 종목 여부

            @JsonProperty("mrkt_warn_cls_code")
            private String marketWarningCode; // 시장경고 구분 코드

            @JsonProperty("short_over_yn")
            private String shortOverYn; // 공매도과열 여부 (Y/N)

            @JsonProperty("sltr_yn")
            private String settlementYn; // 정리매매 여부 (Y/N)

            @JsonProperty("mang_issu_cls_code")
            private String managementIssueCode; // 관리종목 구분 코드

            // 기타
            @JsonProperty("new_hgpr_lwpr_cls_code")
            private String newHighLowCode;   // 신고가/신저가 구분

            @JsonProperty("elw_pblc_yn")
            private String elwPublicYn; // ELW 발행 여부 (Y/N)

            @JsonProperty("stck_fcam")
            private Integer faceValue; // 액면가

            @JsonProperty("stck_sspr")
            private Long substitutionPrice; // 대용가

            @JsonProperty("aspr_unit")
            private Integer askingPriceUnit; // 호가 단위

            @JsonProperty("hts_deal_qty_unit_val")
            private Integer tradingUnit; // 거래 단위

            @JsonProperty("cpfn")
            private Long capitalFund; // 자본금 (단위: 백만원)

            @JsonProperty("cpfn_cnnm")
            private String capitalFundDisplay; // 자본금 표시 (7,780 억)

            @JsonProperty("rstc_wdth_prc")
            private Long restrictionWidth; // 제한폭 가격

            @JsonProperty("fcam_cnnm")
            private String faceValueDisplay; // 액면가 표시

            @JsonProperty("stac_month")
            private String settlementMonth; // 결산 월

            @JsonProperty("whol_loan_rmnd_rate")
            private Double wholeLoanRemainRate; // 전체 융자 잔고 비율

            @JsonProperty("last_ssts_cntg_qty")
            private Long lastShortSellingQty; // 최종 공매도 체결량

        }
    }



    /**
     * 종목 상세페이지 종목 정보 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Detail {

        // 종목 기본 정보

        // [1] 종목명 헤더
        private String stockCode; // stck_shrn_iscd
        private Long currentPrice; // stck_prpr
        private String marketIndexName; // rprs_mrkt_kor_name

        // [2] 종목 카드
        private String sectorName; // bstp_kor_isnm
        private Integer faceValue; // stck_fcam
        private Long marketCap; // hts_avls
        private Long listedShares; // lstn_stcn

        //투자지표

        // [1] 가격
        private Long week52High; // w52_hgpr
        private Long week52Low; // w52_lwpr
        private Long days250High; // d250_hgpr
        private Long days250Low; // d250_lwpr

        // [2] 가치평가
        private Double per; // per
        private Double pbr; // pbr
        private Double eps; // eps
        private Double bps; // bps

        //[3] 수급 거래
        private Long foreignNetBuyQty; // frgn_ntby_qty
        private Long programNetBuyQty; // pgtr_ntby_qty
        private Double foreignOwnershipRate; // hts_frgn_ehrt
        private Double volumeTurnoverRate; // vol_tnrt

        //[4] 리스크 상태
        private String tempStopYn; // temp_stop_yn
        private String investmentCautionYn; // invt_caful_yn
        private String shortOverYn; // short_over_yn
        private String managementIssueCode; // mang_issu_cls_code
    }


    /**
     * 차트(캔들)정보 조회 결과를 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CandleRp {

        @JsonProperty("output2")
        private List<Candle> candles;

        @JsonProperty("rt_cd")
        private String rtCd;  // 응답 코드

        @JsonProperty("msg_cd")
        private String msgCd;  // 메시지 코드

        @JsonProperty("msg1")
        private String msg1;  // 메시지

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Candle {

            @JsonProperty("stck_bsop_date")
            private String tradingDate; // 거래일자 (yyyyMMdd)

            @JsonProperty("stck_oprc")
            private Long openPrice; // 시가

            @JsonProperty("stck_hgpr")
            private Long highPrice; // 고가

            @JsonProperty("stck_lwpr")
            private Long lowPrice; // 저가

            @JsonProperty("stck_clpr")
            private Long closePrice; // 종가

            @JsonProperty("acml_vol")
            private Long accumulatedVolume;  // 누적 거래량
        }
    }


    /**
     * 차트(캔들)정보 조회 결과를 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Candle {

        private String tradingDate; // stck_bsop_date
        private Long openPrice; // stck_oprc
        private Long highPrice; // stck_hgpr
        private Long lowPrice; // stck_lwpr
        private Long closePrice; // stck_clpr
        private Long accumulatedVolume;  // acml_vol
    }
}
