package app.finup.common.constant;

import lombok.Getter;

/**
 * 사용할 비동기 모드 목록을 관리하는 상수 클래스
 * @author kcw
 * @since 2025-12-25
 */

@Getter
public final class AsyncMode {

    public static final String NORMAL = "normalMode";
    public static final String NEWS = "newsMode";
    public static final String STOCK = "stockMode";
}

