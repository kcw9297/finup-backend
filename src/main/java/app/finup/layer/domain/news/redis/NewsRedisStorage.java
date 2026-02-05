package app.finup.layer.domain.news.redis;


import app.finup.layer.domain.news.dto.NewsAiDto;

import java.util.List;

/**
 * 뉴스 관련 정보를 Redis와 직접 조작하는 기능 제공 Storage 인터페이스
 * @author kcw
 * @since 2026-01-07
 */
public interface NewsRedisStorage {


    /**
     * 이전 뉴스 분석 정보 저장
     * @param newsId      대상 뉴스번호
     * @param memberId    요청 회원번호
     * @param analyzation 분석 내용
     */
    void storePrevAnalyze(Long newsId, Long memberId, String analyzation);


    /**
     * 이전 뉴스 분석 기록 조회
     * @param newsId 대상 뉴스번호
     * @param memberId 요청 회원번호
     * @return 이전 분석정보 문자열
     */
    String getPrevAnalyze(Long newsId, Long memberId);


    /**
     * 현재 뉴스에 추천된 단어 저장
     * @param newsId    추천 대상 뉴스번호
     * @param memberId  퀴즈 응시대상 회원번호
     * @param wordNames 추천된 단어명 목록
     */
    void storePrevAnalysisWords(Long newsId, Long memberId, List<NewsAiDto.AnalysisWords> wordNames);


    /**
     * 현재 뉴스에 대해 현재 사용자에게 추천되었던 단어번호 목록 조회
     * @param newsId   추천 대상 뉴스번호
     * @param memberId 퀴즈 응시대상 회원번호
     * @return 이전에 추천된 단어번호 록록
     */
    List<String> getPrevAnalysisWords(Long newsId, Long memberId);

}
