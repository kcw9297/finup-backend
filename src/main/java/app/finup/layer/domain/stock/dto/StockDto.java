package app.finup.layer.domain.stock.dto;

import lombok.*;
import java.util.List;

/**
 * 종목 DTO 클래스
 * @author lky
 * @since 2025-12-01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockDto {

    /**
     * 종목 시가총액 순위 리스트 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketCapRow {

        private String mkscShrnIscd;          // 유가증권 단축 종목코드
        private String dataRank;              // 데이터 순위
        private String htsKorIsnm;            // HTS 한글 종목명
        private String stckPrpr;              // 주식 현재가
        private String prdyVrss;              // 전일 대비
        private String prdyVrssSign;          // 전일 대비 부호
        private String prdyCtrt;              // 전일 대비율
        private String stckAvls;              // 시가 총액
        private String mrktWholAvlsRlim;      // 시장 전체 시가총액 비중

    }

    /**
     * 종목 거래대금 순위 리스트 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TradingValueRow {
        private String htsKorIsnm;                // HTS 한글 종목명
        private String mkscShrnIscd;              // 유가증권 단축 종목코드
        private String dataRank;                  // 데이터 순위

        private String stckPrpr;                  // 주식 현재가
        private String prdyVrssSign;              // 전일 대비 부호
        private String prdyVrss;                  // 전일 대비
        private String prdyCtrt;                  // 전일 대비율

        private String acmlTrPbmn;                // 누적 거래 대금
        private String avrgVol;                   // 평균 거래량

    }

    /**
     * 종목 상세페이지 종목 정보 담기 위해 사용 // 근데 종목 한글이름은 안넘겨줌...
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Detail {

        //종목 기본 정보
        //[1] 종목명 헤더
        private String htsKorIsnm;                // 한글 종목명
        private String stckShrnIscd;              // 주식 단축 종목코드 //005930
        private String stckPrpr;                  // 주식 현재가 //104900
        private String rprsMrktKorName;           // 대표 시장 한글 명 //KOSPI200

        //[2] 종목 카드
        private String bstpKorIsnm;               // 업종 한글 종목명 //전기·전자
        private String stckFcam;                  // 주식 액면가 //100
        private String htsAvls;                   // HTS 시가총액 //6209700
        private String lstnStcn;                  // 상장 주수 //5919637922

        //투자지표
        //[1] 가격
        private String w52Hgpr;                    // 52주일 최고가 //112400
        private String w52Lwpr;                    // 52주일 최저가 //50800
        private String d250Hgpr;                   // 250일 최고가 //112400
        private String d250Lwpr;                   // 250일 최저가 //50800

        //[2] 가치평가
        private String per;                        // PER //21.19
        private String pbr;                        // PBR //1.81
        private String eps;                        // EPS //4950.00
        private String bps;                        // BPS //57930.00

        //[3] 수급 거래
        private String frgnNtbyQty;                 // 외국인 순매수 수량 //0
        private String pgtrNtbyQty;                 // 프로그램매매 순매수 수량 //-48796
        private String htsFrgnEhrt;                 // HTS 외국인 소진율 //52.22
        private String volTnrt;                     // 거래량 회전율 //0.20

        //[4] 리스크 상태
        private String tempStopYn;                   // 임시 정지 여부  //N
        private String invtCafulYn;                  // 투자유의여부 //N
        private String shortOverYn;                  // 단기과열여부 //N
        private String mangIssuClsCode;              // 관리종목여부 //N

    }

    /**
     * 종목 추천 영상 유튜브 정보 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class YoutubeVideo {

        private String keyword;       // 키워드
        private String videoId;       // 비디오ID
        private String title;         // 제목
        private String channelTitle;  // 채널명
        private String thumbnailUrl;  // 썸네일

    }

    /**
     *  API 원본 JSON 매핑 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YoutubeSearchResponse {
        private List<Item> items;

        @Data
        public static class Item {
            private Id id;
            private Snippet snippet;
        }

        @Data
        public static class Id {
            private String videoId;
        }

        @Data
        public static class Snippet {
            private String title;
            private String channelTitle;
            private Thumbnails thumbnails;
            private String description;
        }

        @Data
        public static class Thumbnails {
            private High high;
        }

        @Data
        public static class High {
            private String url;
        }
    }

}
