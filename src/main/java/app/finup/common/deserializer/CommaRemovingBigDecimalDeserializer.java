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

        // 값 조회
        String value = p.getText();

        // 역직렬화 수행
        return Objects.isNull(value) || value.trim().isEmpty() || "0".equals(value) ?
                BigDecimal.ZERO : // 유효하지 않은 입력은 ZERO
                new BigDecimal(value.replace(",", "")); // 값 사이 콤마 제거
    }
}