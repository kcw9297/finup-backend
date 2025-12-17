package app.finup.layer.domain.videolink.manager;

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
     *
     * @param studyName       대상 학습명
     * @param studySummary    대상 학습 요약내용
     * @param studyDetail     대상 학습 상세내용
     * @param studyLevel      강의 수준
     * @param latestSentences 최근 검색 쿼리(문장)
     * @return AI가 생성한 답변 문자열 (JSON 아님)
     */
    String recommendSentenceForStudy(String studyName, String studySummary, String studyDetail, Integer studyLevel, String latestSentences);

}
