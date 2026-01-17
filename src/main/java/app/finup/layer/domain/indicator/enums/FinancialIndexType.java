package app.finup.layer.domain.indicator.enums;

import lombok.Getter;

/**
 * 금융 지표 정보를 나타내는 Enum 상수 클래스
 * @author kcw
 * @since 2026-01-15
 */

@Getter
public enum FinancialIndexType {

    JPY("JPY(100)"), USD("USD");

    private final String value;

    FinancialIndexType(String value) {
        this.value = value;
    }
}
