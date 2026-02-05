package app.finup.layer.domain.words.repository;

import app.finup.layer.domain.words.dto.WordsDto;

import java.util.List;

/**
 * JDBC를 직접 사용하는 Native Query를 취급하는 인터페이스
 * @author kcw
 * @since 2025-02-05
 */
public interface WordsJdbcRepository {

    /**
     * 임베딩 배열 기반 Vector 거리 검색 (검색어 기반)
     * @param keyword   검색어
     * @param embedding 검색어 임베딩 vector 배열
     * @param lim       검색 한도 개수
     * @return 검색 단어 DTO 목록
     */
    List<WordsDto.Row> findWithSimilarByKeyword(String keyword, byte[] embedding, int lim);
}
