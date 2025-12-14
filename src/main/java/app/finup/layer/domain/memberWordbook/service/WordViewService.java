package app.finup.layer.domain.memberWordbook.service;

import app.finup.layer.domain.memberWordbook.dto.WordViewDto;

import java.util.List;

/**
 * 최근 본 단어 서비스
 * @author khj
 * @since 2025-12-14
 */
public interface WordViewService {

    /**
     * 단어 조회 기록
     * @param termId 단어 ID
     */
    void record(Long termId);

    /**
     * 내 최근 본 단어 목록 조회
     * @return 최근 본 단어 리스트
     */
    List<WordViewDto.Row> getMyRecentWords();
}
