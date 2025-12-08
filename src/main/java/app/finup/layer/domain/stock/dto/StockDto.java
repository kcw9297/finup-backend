package app.finup.layer.domain.stock.dto;

import lombok.*;

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
        //private String acmlVol;               // 누적 거래량
        //private String lstnStcn;              // 상장 주수
        private String stckAvls;              // 시가 총액
        private String mrktWholAvlsRlim;      // 시장 전체 시가총액 비중

    }

    /**
     * 종목 거래량 순위 리스트 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    //@NoArgsConstructor
    //@Builder
    public static class VolumeRow {

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

        /* api로 가져올 수 있는 전체 데이터
        //private String iscdStatClsCode;           // 종목 상태 구분 코드
        //private String margRate;                  // 증거금 비율
        private String rprsMrktKorName;           // 대표 시장 한글 명 //KOSPI200
        //private String newHgprLwprClsCode;       // 신 고가 저가 구분 코드
        private String bstpKorIsnm;               // 업종 한글 종목명 //전기·전자
        private String tempStopYn;                // 임시 정지 여부  //N
        //private String oprcRangContYn;            // 시가 범위 연장 여부
        //private String clprRangContYn;            // 종가 범위 연장 여부
        //private String crdtAbleYn;                // 신용 가능 여부
        //private String grmnRateClsCode;           // 보증금 비율 구분 코드
        //private String elwPblcYn;                 // ELW 발행 여부
        private String stckPrpr;                  // 주식 현재가 //104900
        //private String prdyVrss;                  // 전일 대비
        //private String prdyVrssSign;              // 전일 대비 부호
        //private String prdyCtrt;                  // 전일 대비율
        //private String acmlTrPbmn;                // 누적 거래 대금
        //private String acmlVol;                   // 누적 거래량
        //private String prdyVrssVolRate;           // 전일 대비 거래량 비율
        //private String stckOprc;                  // 주식 시가2
        //private String stckHgpr;                  // 주식 최고가
        //private String stckLwpr;                  // 주식 최저가
        //private String stckMxpr;                  // 주식 상한가
        //private String stckLlam;                  // 주식 하한가
        //private String stckSdpr;                  // 주식 기준가
        //private String wghnAvrgStckPrc;           // 가중 평균 주식 가격
        private String htsFrgnEhrt;               // HTS 외국인 소진율 //52.22
        private String frgnNtbyQty;               // 외국인 순매수 수량 //0
        private String pgtrNtbyQty;               // 프로그램매매 순매수 수량 //-48796
        //private String pvtScndDmrsPrc;            // 피벗 2차 디저항 가격
        //private String pvtFrstDmrsPrc;            // 피벗 1차 디저항 가격
        //private String pvtPontVal;                // 피벗 포인트 값
        //private String pvtFrstDmspPrc;            // 피벗 1차 디지지 가격
        //private String pvtScndDmspPrc;            // 피벗 2차 디지지 가격
        //private String dmrsVal;                   // 디저항 값
        //private String dmspVal;                   // 디지지 값
        //private String cpfn;                       // 자본금
        //private String rstcWdthPrc;               // 제한 폭 가격
        private String stckFcam;                  // 주식 액면가 //100
        //private String stckSspr;                  // 주식 대용가
        //private String asprUnit;                  // 호가단위
        //private String htsDealQtyUnitVal;         // HTS 매매 수량 단위 값
        private String lstnStcn;                  // 상장 주수 //5919637922
        private String htsAvls;                   // HTS 시가총액 //6209700
        private String per;                        // PER //21.19
        private String pbr;                        // PBR //1.81
        //private String stacMonth;                 // 결산 월
        private String volTnrt;                   // 거래량 회전율 //0.20
        private String eps;                        // EPS //4950.00
        private String bps;                        // BPS //57930.00
        private String d250Hgpr;                   // 250일 최고가 //112400
        //private String d250HgprDate;               // 250일 최고가 일자
        //private String d250HgprVrssPrprRate;       // 250일 최고가 대비 현재가 비율
        private String d250Lwpr;                   // 250일 최저가 //50800
        //private String d250LwprDate;               // 250일 최저가 일자
        //private String d250LwprVrssPrprRate;       // 250일 최저가 대비 현재가 비율
        //private String stckDryyHgpr;               // 주식 연중 최고가
        //private String dryyHgprVrssPrprRate;       // 연중 최고가 대비 현재가 비율
        //private String dryyHgprDate;               // 연중 최고가 일자
        //private String stckDryyLwpr;               // 주식 연중 최저가
        //private String dryyLwprVrssPrprRate;       // 연중 최저가 대비 현재가 비율
        //private String dryyLwprDate;               // 연중 최저가 일자
        private String w52Hgpr;                     // 52주일 최고가 //112400
        //private String w52HgprVrssPrprCtrt;         // 52주일 최고가 대비 현재가 대비
        //private String w52HgprDate;                 // 52주일 최고가 일자
        private String w52Lwpr;                     // 52주일 최저가 //50800
        //private String w52LwprVrssPrprCtrt;         // 52주일 최저가 대비 현재가 대비
        //private String w52LwprDate;                 // 52주일 최저가 일자
        //private String wholLoanRmndRate;            // 전체 융자 잔고 비율
        //private String sstsYn;                       // 공매도가능여부
        private String stckShrnIscd;                 // 주식 단축 종목코드 //005930
        //private String fcamCnnm;                     // 액면가 통화명
        //private String cpfnCnnm;                     // 자본금 통화명
        //private String apprchRate;                   // 접근도
        //private String frgnHldnQty;                  // 외국인 보유 수량
        //private String viClsCode;                    // VI적용구분코드
        //private String ovtmViClsCode;                // 시간외단일가VI적용구분코드
        //private String lastSstsCntgQty;              // 최종 공매도 체결 수량
        private String invtCafulYn;                  // 투자유의여부 //N
        //private String mrktWarnClsCode;              // 시장경고코드
        private String shortOverYn;                  // 단기과열여부 //N
        //private String sltrYn;                       // 정리매매여부
        private String mangIssuClsCode;              // 관리종목여부 //N
         */
    }

    /**
     * 종목 리스트 조회 RequestHeader를 담기 위해 사용

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class Header {
        private String contentType = "application/json; charset=utf-8";       // content-type
        private String authorization;     // 접근토큰
        private String appkey;            // 앱키
        private String appsecret;         // 앱시크릿키
        @Nullable
        private String personalseckey;    // 고객식별키
        private String trId;              // 거래ID
        @Nullable
        private String trCont;            // 연속 거래 여부
        private String custtype;          // 고객 타입
        @Nullable
        private String seqNo;             // 일련번호
        @Nullable
        private String macAddress;        // 맥주소
        @Nullable
        private String phoneNumber;       // 핸드폰번호
        @Nullable
        private String ipAddr;            // 접속 단말 공인 IP
        @Nullable
        private String gtUid;             // Global UID
    }*/

    /**
     * 종목 리스트 조회 RequestQueryParam 담기 위해 사용

    @Data
    public class RequestQueryParam {
        private String fidInputPrice2;
        private String fidCondMrktDivCode;
        private String fidCondScrDivCode;
        private String fidDivClsCode;
        private String fidInputIscd;
        private String fidTrgtClsCode;
        private String fidTrgtExlsClsCode;
        private String fidInputPrice1;
        private String fidVolCnt;
    } */

}
