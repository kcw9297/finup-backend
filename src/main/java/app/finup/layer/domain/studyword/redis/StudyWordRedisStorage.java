package app.finup.layer.domain.studyword.redis;


import java.util.List;

/**
 * 학습 단어 정보와 관련한 Redis 조작 기능을 다루는 인터페이스
 * @author kcw
 * @since 2025-12-16
 */
public interface StudyWordRedisStorage {

    /**
     * 현재 사용자에게 추천되었던 단어 이력 저장
     * @param studyWordIds 추천된 단어 고유번호 목록 (문자열 변환 필수)
     * @param memberId 추천 대상 회원번호
     */
    void storeLatestRecommendedIds(List<String> studyWordIds, Long memberId);


    /**
     * 현재 사용자에게 추천되었던 단어 번호 목록 조회
     * @param memberId 추천 대상 회원번호
     */
    List<Long> getLatestRecommendedIds(Long memberId);

}
