package app.finup.layer.domain.studyprogress.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.study.repository.StudyRepository;
import app.finup.layer.domain.studyprogress.dto.StudyProgressDto;
import app.finup.layer.domain.studyprogress.dto.StudyProgressDtoMapper;
import app.finup.layer.domain.studyprogress.entity.StudyProgress;
import app.finup.layer.domain.studyprogress.enums.StudyStatus;
import app.finup.layer.domain.studyprogress.repository.StudyProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * StudyProgressService 구현 클래스
 * @author kcw
 * @since 2025-12-03
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudyProgressServiceImpl implements StudyProgressService {

    private final StudyProgressRepository studyProgressRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StudyProgressDto.Row> getListByMemberId(Long memberId) {

        return studyProgressRepository
                .findByMemberId(memberId)
                .stream()
                .map(StudyProgressDtoMapper::toRow)
                .toList();
    }


    @Override
    public void progress(Long studyId, Long memberId) {

        // [1] entity 조회
        StudyProgress studyProgress =
                studyProgressRepository
                        .findByStudyIdAndMemberId(studyId, memberId)
                        .orElse(null);

        // [2] 만약 엔티티가 존재하지 않는 경우, 새롭게 생성 (단계 학습을 최초 열람한 경우)
        if (Objects.isNull(studyProgress))
            studyProgressRepository.save(createNewProgress(studyId, memberId, StudyStatus.IN_PROGRESS));
    }


    // 새로운 학습 진도 엔티티 생성
    private StudyProgress createNewProgress(Long studyId, Long memberId, StudyStatus studyStatus) {

        // [1] 필요한 엔티티 조회
        Study study = studyRepository
                .findById(studyId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));

        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));

        // [2] 엔티티 생성 및 반환
        return StudyProgress.builder()
                .study(study)
                .member(member)
                .studyStatus(studyStatus)
                .build();
    }


    @Override
    public void complete(Long studyId, Long memberId) {

        // [1] entity 조회
        StudyProgress studyProgress =
                studyProgressRepository
                        .findByStudyIdAndMemberId(studyId, memberId)
                        .orElse(null);

        // [2] 만약 엔티티가 존재하지 않는 경우, 새롭게 생성 (단계 학습을 최초 열람한 경우)
        if (Objects.isNull(studyProgress))
            studyProgressRepository.save(createNewProgress(studyId, memberId, StudyStatus.COMPLETED));

        // [3] 이미 존재하는 경우, 상태만 새롭게 생신
        else studyProgress.complete();
    }
}