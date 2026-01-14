package app.finup.common.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * BigDecimal 역직렬화 시 숫자 사이의 콤마(,)를 제거하는 역직렬화 클래스
 * @author kcw
 * @since 2026-01-14
 */

public class CommaRemovingBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        // [1] 값 추출 후 검증 (0이거나 비어있는 경우, 혹은 유효하지 않은 숫자는 ZERO)
        String value = p.getText();
        if (Objects.isNull(value) || value.trim().isEmpty() || "0".equals(value)) return BigDecimal.ZERO;

        // [2] 추출한 값에서 콤마(,) 를 제거하고 반환
        return new BigDecimal(value.replace(",", ""));
    }
}