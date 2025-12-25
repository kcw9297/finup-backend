package app.finup.layer.domain.memberWordbook.enums;

import lombok.Getter;

/**
 * 단어장 단어 암기 열거형
 * @author khj
 * @since 2025-12-25
 */

@Getter
public enum MemorizeStatus {
    NONE("암기 전"),
    MEMORIZED("암기 완료");

    private final String value;

    MemorizeStatus(String value) {this.value = value;}
}
