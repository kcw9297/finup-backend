package app.finup.layer.domain.studyword.manager;

import java.util.List;

/**
 * 학습단어 관련 AI 채팅 기능을 제공하는 인터페이스
 * @author kcw
 * @since 2025-12-16
 */

public interface StudyWordAiManager {

    /**
     * 유사도 검색을 단어 중 추천 로직 수행
     * @param json AI 제공 데이터 JSON 문자열
     * @return AI가 선택한 데이터 고유번호
     */
    List<Long> recommendForStudy(String json);

}
