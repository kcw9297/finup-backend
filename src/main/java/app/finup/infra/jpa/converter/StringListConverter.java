package app.finup.infra.jpa.converter;

import app.finup.common.utils.StrUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;
import java.util.Objects;

/**
 * JPA Entity 와 데이터베이스 간 문자열 리스트 타입 변환기 클래스
 * @author kcw97
 * @since 2025-12-11
 */

@Converter(autoApply = true)
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attributes) {
        return Objects.isNull(attributes) || attributes.isEmpty() ?
                null : StrUtils.toJson(attributes);
    }

    @Override
    public List<String> convertToEntityAttribute(String strList) {
        return Objects.isNull(strList) || strList.isBlank() ?
                List.of() : StrUtils.fromJson(strList, new TypeReference<>(){});
    }
}