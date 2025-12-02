package app.finup.layer.domain.stocks.dto;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 종목 DTO 클래스
 * @author lky
 * @since 2025-12-01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StocksDto {

    /**
     * 종목 리스트 조회 RequestHeader를 담기 위해 사용
     */
    @Data
    public class Row {
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
    }

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
    }
}
