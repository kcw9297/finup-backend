package app.finup.layer.domain.study.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.study.dto.StudyDto;
import app.finup.layer.domain.study.dto.StudyDtoMapper;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.study.repository.StudyRepository;
import app.finup.layer.domain.study.mapper.StudyMapper;
import app.finup.layer.domain.studyprogress.repository.StudyProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * StudyService 구현 클래스
 * @author kcw
 * @since 2025-12-03
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;
    private final StudyProgressRepository studyProgressRepository;
    private final StudyMapper studyMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<StudyDto.Row> getPagedList(StudyDto.Search rq) {

        // [1] 페이징 쿼리 수행
        List<StudyDto.Row> rows = studyMapper.search(rq);
        Integer count = studyMapper.countForSearch(rq);

        // [2] 결과 기반 페이징 객체 생성 및 반환
        return Page.of(rows, count, rq.getPageNum(), rq.getPageSize());
    }


    @Override
    @Transactional(readOnly = true)
    public StudyDto.Detail getDetail(Long studyId) {

        return studyRepository
                .findById(studyId)
                .map(StudyDtoMapper::toDetail)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));
    }


    @Override
    public Long add(StudyDto.Add rq) {

        // [1] 엔티티 생성
        Study study = Study.builder()
                .name(rq.getName())
                .summary(rq.getSummary())
                .level(rq.getLevel())
                .build();

        // [2] 엔티티 저장
        return studyRepository.save(study).getStudyId();
    }


    @Override
    public void edit(StudyDto.Edit rq) {

        // [1] 엔티티 조회
        Study study = studyRepository
                .findById(rq.getStudyId())
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));

        // [2] 정보 갱신 수행
        Integer prevLevel = study.getLevel();
        Integer afterLevel = rq.getLevel();
        study.edit(rq.getName(), rq.getSummary(), afterLevel);

        // [3] 난이도(level)가 변경된 경우, 사용자 진도 정보 일괄 삭제
        if (!Objects.equals(prevLevel, afterLevel))
            studyProgressRepository.deleteByStudyId(rq.getStudyId());
    }


    @Override
    public void remove(Long studyId) {
        studyRepository.deleteById(studyId);
    }
}
