package app.finup.common.enums;

import lombok.Getter;

/**
 * 로그에 사용할 Emoji 모음
 * @author kcw
 * @since 2026-01-23
 */
@Getter
public enum LogEmoji {

    OK("✅"),
    WARN("⚠️"),
    STOP("🛑"),
    ALERT("🚨"),
    LOCK("🔒"),
    UNLOCK("🔓"),
    ANALYSIS("📊"),
    TRY("🚀");

    private final String value;

    LogEmoji(String value) {
        this.value = value;
    }
}
