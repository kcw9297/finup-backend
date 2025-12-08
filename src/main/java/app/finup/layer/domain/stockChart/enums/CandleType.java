package app.finup.layer.domain.stockChart.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum CandleType {
    DAY("day","D"),
    WEEK("week","W"),
    MONTH("month","M");

    private final String param;
    private final String kisCode;

    CandleType(String param, String kisCode) {
        this.param = param;
        this.kisCode = kisCode;
    }

    public String getKisCode() {
        return kisCode;
    }

    public static CandleType fromParam(String param) {
        for (CandleType type : values()) {
            if (type.param.equalsIgnoreCase(param)) {
                return type;
            }
        }
        log.info("입력된 candleType param = {}", param);
        throw new IllegalArgumentException("지원하지 않는 캔들 타입: " + param);
    }
}
