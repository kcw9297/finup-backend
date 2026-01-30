package app.finup.layer.domain.words.service;

import app.finup.layer.domain.words.dto.WordsAiDto;

import java.util.List;

/**
 * 금융 용어 사전 AI 로직 인터페이스
 * @author kcw
 * @since 2026-01-26
 */
public interface WordsAiService {

    /**
     * 뉴스 추천 단어 조회
     * @param newsId 대상 뉴스번호
     * @param memberId 요청 회원번호
     * @return 추천 단어 목록 (캐싱 데이터)
     */
    List<WordsAiDto.Recommendation> getRecommendationNewsWords(Long newsId, Long memberId);


    /**
     * 뉴스 추천 단어 조회 (재추천)
     * @param newsId 대상 뉴스번호
     * @param memberId 요청 회원번호
     * @return 재추천 단어 목록
     */
    List<WordsAiDto.Recommendation> retryAndGetRecommendationNewsWords(Long newsId, Long memberId);

}
