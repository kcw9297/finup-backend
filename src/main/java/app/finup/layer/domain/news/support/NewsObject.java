package app.finup.layer.domain.news.support;

/**
 * 뉴스 객체가 공유하는 메소드를 정의하기 위한 인터페이스
 * @author kcw
 * @since 2026-01-28
 */
public interface NewsObject {

    String getLink();

    String getTitle();

    String getSummary();

    String getDescription();
}
