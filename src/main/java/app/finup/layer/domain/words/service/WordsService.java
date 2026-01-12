package app.finup.layer.domain.words.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.words.dto.WordsDto;

import java.util.List;

/**
 * 금융 용어 사전 비즈니스 로직 인터페이스
 * @author khj
 * @since 2025-12-10
 */

public interface WordsService {

    /**
     * 학습 단어 초기화 (존재하지 않을 시 최초 1회)
     */
    void initWords();


    /**
     * 금융 용어 검색
     * @param rq 검색 요청 DTO (filter, keyword, pageNum, pageSize 포함)
     * @return 페이징된 검색 결과(용어 목록) DTO 리스트
     */
    Page<WordsDto.Row> search(WordsDto.Search rq, Long memberId);


    /**
     * 단어장 홈 관련 서비스 메소드
     * @return 페이징된 검색 결과(용어 목록) DTO 리스트
     */
    List<WordsDto.Row> getHomeWords();


    /**
     * 단어 상세 조회
     * @param termId 단어 번호
     * @return 단어 상세 정보
     */
    WordsDto.Row getDetail(Long termId);


    /**
     * Redis 최근 단어 추가 메소드
     */
    List<String> getRecent(Long memberId);


    /**
     * Redis 최근 단어 기록 delete 메소드
     */
    void clear(Long memberId);


    /**
     * Redis 최근 검색 단어 삭제 메소드
     */
    void removeRecent(Long memberId, String keyword);
}
