package app.finup.common.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * LocalDate 역직렬화 클래스 (yyyyMMdd 형태)
 * @author kcw
 * @since 2026-01-14
 */

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    // yyyyMMdd 포메터
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        // 값 조회
        String dateStr = p.getText();

        // 역직렬화 수행
        return Objects.isNull(dateStr) || dateStr.isEmpty() || "0".equals(dateStr) ?
            null : LocalDate.parse(dateStr, FORMATTER);
    }
}