package app.finup.layer.domain.videolink.manager;

import java.util.List;

/**
 * 학습영상 관련 AI 채팅 기능을 제공하는 인터페이스
 * @author kcw
 * @since 2025-12-16
 */

public interface VideoLinkAiManager {

    /**
     * 페이지 홈의 추천 영상 유사도 검색을 위한 추천 검색 문장 생성
     * @return AI가 생성한 답변 문자열 (JSON 아님)
     */
    String recommendSentenceForLogoutHome();


    /**
     * 페이지 홈의 추천 영상 유사도 검색을 위한 추천 검색 문장 생성 (재시도)
     * @param latestSentences 최근 검색 쿼리(문장)
     * @return AI가 생성한 답변 문자열 (JSON 아님)
     */
    String recommendSentenceForLoginHome(String latestSentences);


    /**
     * 페이지 홈의 추천 영상 유사도 검색을 위한 추천 검색 문장 생성
     * @param json AI 제공 데이터 JSON 문자열
     * @return AI가 선택한 데이터 고유번호
     */
    List<Long> recommendForStudy(String json);

}
