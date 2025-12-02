package app.finup.layer.domain.videolink.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.studybookmark.dto.StudyBookmarkDto;

/**
 * 단계학습 북마크 로직처리 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyBookmarkService {

    /**
     * 페이징 리스트 조회
     * @param rq 페이징 요청 DTO
     */
    Page<StudyBookmarkDto.Summary> getPagedList(StudyBookmarkDto.Search rq);
}
