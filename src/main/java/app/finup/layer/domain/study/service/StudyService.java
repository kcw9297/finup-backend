package app.finup.layer.domain.study.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.study.dto.StudyDto;

import java.util.List;

/**
 * 단계별 학습 핵심로직 처리 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyService {

    Page<StudyDto.Row> getPagedList(StudyDto.Search rq);

    StudyDto.Detail getDetail(Long studyId);

    void add(StudyDto.Add rq);

    void edit(StudyDto.Edit rq);

    void remove(Long studyId);
}
