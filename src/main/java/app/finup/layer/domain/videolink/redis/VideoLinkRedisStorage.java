package app.finup.layer.domain.videolink.redis;


import java.util.List;

/**
 * 학습 영상 정보와 관련한 Redis 조작 기능을 다루는 인터페이스
 * @author kcw
 * @since 2025-12-16
 */
public interface VideoLinkRedisStorage {

    /**
     * 현재 사용자에게 추천되었던 영상 이력 저장 (최근 30개 까지만 저장)
     * @param videoLinkIds 추천된 영상 고유번호 목록 (문자열 변환 필수)
     * @param memberId 추천 대상 회원번호
     */
    void storeLatestRecommendedIds(List<String> videoLinkIds, Long memberId);


    /**
     * 현재 사용자에게 추천되었던 영상 번호 목록 조회 (최근 30개)
     * @param memberId 추천 대상 회원번호
     */
    List<Long> getLatestRecommendedIds(Long memberId);


    /**
     * 페이지 홈에 게시하기 위해 추천된 AI 문장 저장
     * @param sentence 생성된 AI 문장
     * @param memberId 추천 대상 회원번호
     */
    void storeLatestSentenceForHome(String sentence, Long memberId);


    /**
     * 페이지 홈에 게시하기 위해 추천된 AI 키워드 일괄 조회
     * @param memberId 추천 대상 회원번호
     * @return 키워드 과거 이력 문장
     */
    String getLatestSentenceForHome(Long memberId);

}
