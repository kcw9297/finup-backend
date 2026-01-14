package app.finup.layer.domain.stock.enums;

import lombok.Getter;

/**
 * 분석하고자 하는 차트 유형을 나타낼 Enum 상수 클래스
 * @author kcw
 * @since 2026-01-05
 */

@Getter
public enum ChartType {
    DAY, WEEK, MONTH;
}
