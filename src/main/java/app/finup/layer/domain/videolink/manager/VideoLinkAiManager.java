package app.finup.layer.domain.videolink.manager;

/**
 * 학습영상 관련 AI 채팅 기능을 제공하는 인터페이스
 * @author kcw
 * @since 2025-12-16
 */

public interface VideoLinkAiManager {

    /**
     * 페이지 홈의 추천 영상 유사도 검색을 위한 추천 키워드 생성
     * @return AI가 생성한 답변 문자열 (JSON 아님)
     */
    String recommendKeywordsForLogoutHome();


    /**
     * 페이지 홈의 추천 영상 유사도 검색을 위한 추천 키워드 생성 (재시도)
     * @param lastestKeywords 최근 검색 키워드
     * @return AI가 생성한 답변 문자열 (JSON 아님)
     */
    String recommendKeywordsForLoginHome(String lastestKeywords);


    /**
     * 페이지 홈의 추천 영상 유사도 검색을 위한 추천 키워드 생성
     * @param lastestKeywords 최근 검색 키워드
     * @return AI가 생성한 답변 문자열 (JSON 아님)
     */
    String recommendKeywordsForStudy(String lastestKeywords);

}
