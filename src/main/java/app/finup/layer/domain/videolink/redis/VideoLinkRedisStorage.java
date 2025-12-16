package app.finup.layer.domain.videolink.redis;


/**
 * 학습 영상 정보와 관련한 Redis 조작 기능을 다루는 인터페이스
 * @author kcw
 * @since 2025-12-16
 */
public interface VideoLinkRedisStorage {

    /**
     * 페이지 홈에 게시하기 위해 추천된 AI 키워드 저장
     * @param keywords 생성된 AI 키워드
     * @param memberId 추천 대상 회원번호
     */
    void storeLatestKeywordsForHome(String keywords, Long memberId);


    /**
     * 페이지 홈에 게시하기 위해 추천된 AI 키워드 일괄 조회
     * @param memberId 추천 대상 회원번호
     * @return 키워드 과거 이력 문자열
     */
    String getLatestKeywordsForHome(Long memberId);


    /**
     * 페이지 홈에 게시하기 위해 추천된 AI 키워드 저장
     * @param keywords 생성된 AI 키워드
     * @param studyId 추천 대상 학습번호
     * @param memberId 추천 대상 회원번호
     */
    void storeLatestKeywordsForStudy(String keywords, Long studyId, Long memberId);


    /**
     * 학습 페이지에 게시하기 위해 추천된 AI 키워드 일괄 조회
     * @param studyId 추천 대상 학습번호
     * @param memberId 추천 대상 회원번호
     * @return 키워드 과거 이력 문자열
     */
    String getLatestKeywordsForStudy(Long studyId, Long memberId);

}
