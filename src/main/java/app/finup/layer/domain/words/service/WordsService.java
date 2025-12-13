package app.finup.layer.domain.words.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.words.dto.WordsDto;

/**
 * 금융 용어 사전 비즈니스 로직 인터페이스
 * @author khj
 * @since 2025-12-10
 */

public interface WordsService {

    /**
    * 금융 용어 사전 초기 적재 및 갱신
    */
    void refreshTerms();  // 외부 Provider -> 내부 DB 갱신

    /**
     * 금융 용어 검색
     * @param rq 검색 요청 DTO (filter, keyword, pageNum, pageSize 포함)
     * @return 페이징된 검색 결과(용어 목록) DTO 리스트
     */
    Page<WordsDto.Row> search(WordsDto.Search rq);


    /**
     * 초기 적재 여부 확인
     * @return true  → DB 비어 있음(초기화 가능)
     *         false → 이미 초기화 완료됨
     */
    Boolean isInitialized();

    /**
    * KB Think 용어 사전 전체 크롤링
    */
    void crawlAllFromKbThink();
}
