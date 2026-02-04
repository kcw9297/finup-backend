package app.finup.api.external.stock.enums;

import lombok.Getter;


/**
 * 조회할 차트 캔들 타입 정보를 표현하는 열거형 타입 클래스
 * @author kcw
 * @since 2025-12-30
 */
@Getter
public enum CandleType {

    DAY("D"),
    WEEK("W"),
    MONTH("M");

    private final String type;

    CandleType(String type) {
        this.type = type;
    }

}
