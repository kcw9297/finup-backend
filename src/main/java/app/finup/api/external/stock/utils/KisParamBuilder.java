package app.finup.api.external.stock.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * KIS API 요청을 위한 파라미터 Map Builder 클래스
 * @author kcw
 * @since 2025-12-28
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KisParamBuilder {

    // 파라미터를 담을 Map 선언
    private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    // 객체 제공 메소드
    public static KisParamBuilder builder() {
        return new KisParamBuilder();
    }

    // Builder 메소드
    // 시장 구분 코드
    public KisParamBuilder marketCode(String code) {
        params.add("FID_COND_MRKT_DIV_CODE", code);
        return this;
    }

    // 화면 구분 코드
    public KisParamBuilder screenCode(String code) {
        params.add("FID_COND_SCR_DIV_CODE", code);
        return this;
    }

    // 분류 코드
    public KisParamBuilder classCode(String code) {
        params.add("FID_DIV_CLS_CODE", code);
        return this;
    }

    // 종목 코드
    public KisParamBuilder stockCode(String code) {
        params.add("FID_INPUT_ISCD", code);
        return this;
    }

    // 주가 구분 타입 (차트에서, 일봉/주봉/월봉 으로 나눌 때 사용. D/W/M 값을 사용)
    public KisParamBuilder periodType(String type) {
        params.add("FID_PERIOD_DIV_CODE", type);
        return this;
    }

    // 차트에서 주가 표시 타입 (0: 수정 주가, 1: 원주가)
    public KisParamBuilder priceAdjustType(String type) {
        params.add("FID_ORG_ADJ_PRC", type);
        return this;
    }

    // 대상 분류 코드
    public KisParamBuilder targetClassCode(String code) {
        params.add("FID_TRGT_CLS_CODE", code);
        return this;
    }

    // 대상 제외 분류 코드
    public KisParamBuilder targetExcludeClassCode(String code) {
        params.add("FID_TRGT_EXLS_CLS_CODE", code);
        return this;
    }

    // 소속 분류 코드
    public KisParamBuilder belongClassCode(String code) {
        params.add("FID_BLNG_CLS_CODE", code);
        return this;
    }

    // 가격1
    public KisParamBuilder inputPrice1(String price) {
        params.add("FID_INPUT_PRICE_1", price);
        return this;
    }

    // 가격2
    public KisParamBuilder inputPrice2(String price) {
        params.add("FID_INPUT_PRICE_2", price);
        return this;
    }

    // 거래량
    public KisParamBuilder volume(String count) {
        params.add("FID_VOL_CNT", count);
        return this;
    }

    // 날짜1
    public KisParamBuilder date1(String date) {
        params.add("FID_INPUT_DATE_1", date);
        return this;
    }

    // 날짜2
    public KisParamBuilder date2(String date) {
        params.add("FID_INPUT_DATE_2", date);
        return this;
    }

    // 빌드 메소드
    // MarketCap(시가총액) 전용 빌드
    public MultiValueMap<String, String> buildForMarketCap() {
        params.addIfAbsent("FID_INPUT_PRICE_1", null);
        params.addIfAbsent("FID_INPUT_PRICE_2", null);
        params.addIfAbsent("FID_VOL_CNT", null);
        return params;
    }

    // TradingValue(거래대금) 전용 빌드
    public MultiValueMap<String, String> buildForTradingValue() {
        params.addIfAbsent("FID_INPUT_PRICE_1", null);
        params.addIfAbsent("FID_INPUT_PRICE_2", null);
        params.addIfAbsent("FID_VOL_CNT", null);
        params.addIfAbsent("FID_INPUT_DATE_1", null);
        return params;
    }

    // 일반 빌드
    public MultiValueMap<String, String> build() {
        return params;
    }


}
