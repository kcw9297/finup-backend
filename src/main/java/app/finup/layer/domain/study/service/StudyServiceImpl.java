package app.finup.layer.domain.study.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.infra.ai.provider.EmbeddingProvider;
import app.finup.common.utils.AiUtils;
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
    private final EmbeddingProvider embeddingProvider;

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

        // [1] 양끝 공백 제거
        String name = rq.getName().trim();
        String summary = rq.getSummary().trim();
        String detail = rq.getDetail().trim();
        Integer level = rq.getLevel();

        // [2] 임베딩 배열 생성
        String text = AiUtils.generateEmbeddingText(name, summary, detail,getLevelText(level));
        byte[] embedding = embeddingProvider.generate(text);

        // [2] 엔티티 생성
        Study study = Study.builder()
                .name(name)
                .summary(summary)
                .detail(detail)
                .level(level)
                .embedding(embedding)
                .build();

        // [3] 엔티티 저장
        return studyRepository.save(study).getStudyId();
    }


    @Override
    public void edit(StudyDto.Edit rq) {

        // [1] 양끝 공백 제거
        String name = rq.getName().trim();
        String summary = rq.getSummary().trim();
        String detail = rq.getDetail().trim();
        Integer level = rq.getLevel();

        // [2] 엔티티 조회
        Study study = studyRepository
                .findById(rq.getStudyId())
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));

        // 변경 내용이 있는지 검증 (없는 경우에 불필요한 API 호출 방지)
        if (Objects.equals(study.getName(), name) &&
                Objects.equals(study.getSummary(), summary) &&
                Objects.equals(study.getDetail(), detail) &&
                Objects.equals(study.getLevel(), rq.getLevel())) throw new BusinessException(AppStatus.STUDY_NOT_UPDATABLE);

        // [3] 임베딩 배열 생성
        String text = AiUtils.generateEmbeddingText(name, summary, detail, getLevelText(level));
        byte[] embedding = embeddingProvider.generate(text);

        // [4] 정보 갱신 수행
        study.edit(name, summary, detail, level, embedding);

        // [5] 사용자 진도 정보 일괄 삭제
        studyProgressRepository.deleteByStudyId(rq.getStudyId());
    }

    // 숫자 레벨 기반 텍스트로 변환 (임베딩 시)
    private String getLevelText(Integer level) {

        return switch(level) {
            case 1 -> "입문 초급";
            case 2 -> "초중급";
            case 3 -> "중급";
            case 4 -> "중고급";
            case 5 -> "고급 실전";
            default -> "";
        };
    }


    @Override
    public void remove(Long studyId) {
        studyRepository.deleteById(studyId);
    }
}
