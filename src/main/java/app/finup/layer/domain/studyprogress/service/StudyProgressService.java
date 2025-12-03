package app.finup.layer.domain.studyprogress.service;

import app.finup.layer.domain.studyprogress.dto.StudyProgressDto;

import java.util.List;

/**
 * 학습 진도 로직처리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyProgressService {

    List<StudyProgressDto.Row> getListByMemberId(Long memberId);

    void progress(Long studyId, Long memberId);

    void complete(Long studyId, Long memberId);
}
