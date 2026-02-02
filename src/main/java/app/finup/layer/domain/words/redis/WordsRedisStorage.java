package app.finup.layer.domain.words.redis;

import java.util.List;

/**
 * 단어 관련 Redis 조작 기능 제공 인터페이스
 * @author khj
 * @since 2025-12-13
 */
public interface WordsRedisStorage {

    /**
     * 이전 검색 단어 저장
     * @param memberId 요청 회원번호
     * @param keyword 검색어
     */
    void storeRecentSearchKeyword(Long memberId, String keyword);


    /**
     * 이전 검색 단어 목록 조회
     * @param memberId 요청 회원번호
     * @param limit 조회 단어 개수
     */
    List<String> getRecentSearchKeywords(Long memberId, Integer limit);


    /**
     * 검색 단어 목록 초기화(모두 삭제)
     * @param memberId 요청 회원번호
     */
    void clearRecentSearchKeywords(Long memberId);


    /**
     * 특정 검색 단어 삭제
     * @param memberId 요청 회원번호
     * @param keyword 검색어
     */
    void removeRecentSearchKeyword(Long memberId, String keyword);


    /**
     * 현재 뉴스에 추천된 단어번호 저장
     * @param newsId 추천 대상 뉴스번호
     * @param memberId 퀴즈 응시대상 회원번호
     * @param termIds 추천된 단어번호 목록
     */
    void storePrevRecommendationIds(Long newsId, Long memberId, List<Long> termIds);


    /**
     * 현재 뉴스에 대해 현재 사용자에게 추천되었던 단어번호 목록 조회
     * @param newsId 추천 대상 뉴스번호
     * @param memberId 퀴즈 응시대상 회원번호
     * @return 이전에 추천된 단어번호 록록
     */
    List<Long> getPrevRecommendationIds(Long newsId, Long memberId);

}
