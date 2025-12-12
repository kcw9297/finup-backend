package app.finup.infra.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;
import java.util.Objects;

/**
 * JPA Entity 와 데이터베이스 간 Duration 타입 변환기 클래스
 * @author kcw97
 * @since 2025-12-11
 */

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return Objects.nonNull(duration) ? duration.toSeconds() : null;
    }

    @Override
    public Duration convertToEntityAttribute(Long seconds) {
        return Objects.nonNull(seconds) ? Duration.ofSeconds(seconds) : null;
    }
}