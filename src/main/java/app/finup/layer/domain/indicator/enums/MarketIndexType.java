package app.finup.layer.domain.indicator.enums;

import lombok.Getter;

/**
 * 주식 시장 지표 정보를 나타내는 Enum 상수 클래스
 * @author kcw
 * @since 2026-01-15
 */

@Getter
public enum MarketIndexType {

    KOSPI("코스피"), KOSDAQ("코스닥");

    private final String value;

    MarketIndexType(String value) {
        this.value = value;
    }
}
