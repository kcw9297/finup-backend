package app.finup.layer.domain.studyword.service;

import app.finup.common.constant.Const;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.manager.FileUrlProvider;
import app.finup.infra.file.manager.FileManager;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.study.repository.StudyRepository;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import app.finup.layer.domain.studyword.dto.StudyWordDtoMapper;
import app.finup.layer.domain.studyword.entity.StudyWord;
import app.finup.layer.domain.studyword.repository.StudyWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * StudyWordService 구현 클래스
 * @author kcw
 * @since 2025-12-03
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudyWordServiceImpl implements StudyWordService {

    // 사용 상수
    private static final Double DEFAULT_DISPLAY_ORDER = 1000.0;
    private static final Double DISPLAY_ORDER_INCREMENT = 1.0; // 삽입 시 증가량

    private final StudyWordRepository studyWordRepository;
    private final StudyRepository studyRepository;
    private final FileManager fileManager;
    private final FileUrlProvider fileUrlProvider;

    @Override
    @Transactional(readOnly = true)
    public List<StudyWordDto.Row> getListByStudy(Long studyId) {

        return studyWordRepository
                .findByStudyId(studyId)
                .stream()
                .map(studyWord -> StudyWordDtoMapper.toRow(studyWord, fileUrlProvider::getFullPath))
                .toList();
    }


    @Override
    public void add(StudyWordDto.Add rq) {

        // [1] 필요 엔티티 조회
        Study study = studyRepository
                .findById(rq.getStudyId())
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));

        // [2] 정렬 순서 계산 (첫 항목: 1000.0, 이후: 이전 값 + 1.0)
        Double displayOrder = Objects.isNull(rq.getLastStudyWordId()) ?
                DEFAULT_DISPLAY_ORDER :
                calculateNextOrder(rq.getLastStudyWordId());

        // [3] 엔티티 생성
        StudyWord studyWord = StudyWord.builder()
                .name(rq.getName())
                .meaning(rq.getMeaning())
                .displayOrder(displayOrder)
                .study(study)
                .build();

        // [4] 엔티티 저장
        studyWordRepository.save(studyWord);
    }


    // 삽입할 단어의 정렬 값 계산
    private Double calculateNextOrder(Long lastStudyWordId) {

        return studyWordRepository
                .findById(lastStudyWordId)
                .map(word -> word.getDisplayOrder() + DISPLAY_ORDER_INCREMENT)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));
    }


    @Override
    public void uploadImage(Long studyWordId, MultipartFile file) {


    }


    @Override
    public void edit(StudyWordDto.Edit rq) {

    }


    @Override
    public void reorder(StudyWordDto.Reorder rq) {

    }


    @Override
    public void remove(Long studyWordId) {

    }


    @Override
    public void removeImage(Long studyWordId) {

    }
}
