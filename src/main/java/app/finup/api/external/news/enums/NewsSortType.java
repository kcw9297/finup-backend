package app.finup.api.external.news.enums;

import lombok.Getter;


/**
 * 네이버 뉴스 API "sort" 파라미터 유형 정보를 담는 열거형 상수 클래스
 * @author kcw
 * @since 2026-01-19
 */
@Getter
public enum NewsSortType {

    RELATED("sim"), LATEST("date");

    private final String type;

    NewsSortType(String type) {
        this.type = type;
    }

}
