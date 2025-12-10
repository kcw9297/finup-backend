package app.finup.layer.domain.studyword.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.base.utils.ReorderUtils;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.study.repository.StudyRepository;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import app.finup.layer.domain.studyword.dto.StudyWordDtoMapper;
import app.finup.layer.domain.studyword.entity.StudyWord;
import app.finup.layer.domain.studyword.repository.StudyWordRepository;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import app.finup.layer.domain.uploadfile.manager.UploadFileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final String CACHE_VALUE = "STUDY_WORDS";

    private final StudyWordRepository studyWordRepository;
    private final StudyRepository studyRepository;
    private final UploadFileManager uploadFileManager; // 파일 엔티티 및 주소 제공


    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CACHE_VALUE,
            key = "#studyId"
    )
    public List<StudyWordDto.Row> getListByStudy(Long studyId) {

        return studyWordRepository
                .findByStudyId(studyId)
                .stream()
                .map(studyWord -> StudyWordDtoMapper.toRow(studyWord, uploadFileManager::getFullUrl))
                .toList();
    }


    @Override
    @CacheEvict(
            value = CACHE_VALUE,
            key = "#rq.studyId"
    )
    public void add(StudyWordDto.Add rq) {

        // [1] 필요 엔티티 조회
        Study study = studyRepository
                .findById(rq.getStudyId())
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));

        // [2] 정렬 순서 계산 (첫 항목: 1000.0, 이후: 이전 값 + 1.0)
        Double displayOrder = studyWordRepository
                .findLastByStudyId(rq.getStudyId())
                .map(ReorderUtils::calculateNextOrder)
                .orElse(ReorderUtils.DEFAULT_DISPLAY_ORDER); // 최초 단어인 경우 기본 값

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


    @Override
    @CacheEvict(
            value = CACHE_VALUE,
            key = "#result"
    )
    public Long uploadImage(Long studyWordId, MultipartFile file) {

        // [1] 단어 정보 조회
        StudyWord studyWord =
                studyWordRepository
                        .findWithImageById(studyWordId)
                        .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 변경 전 원래 이미지 정보 추출 후, 소유자를 null로 변경
        UploadFile oldImageFile = studyWord.getWordImageFile();
        oldImageFile.remove(); // Soft Delete 처리 (나중에 스케줄러에서 파일 삭제)

        // [3] 새롭게 등록하는 파일 엔티티 생성 후, 삽입
        UploadFile newImageFile = uploadFileManager.setEntity(file, studyWordId, FileOwner.STUDY_WORD, FileType.UPLOAD);
        studyWord.uploadImage(newImageFile); // cascade 옵션으로 인해 자동 저장됨

        // [4] 새롭게 추가된 파일 생성
        uploadFileManager.store(file, newImageFile.getFilePath());

        // [5] 캐싱 처리를 위한 학습번호 반환 (#result 에서 감지)
        return studyWord.getStudy().getStudyId();
    }


    @Override
    public void edit(StudyWordDto.Edit rq) {

        studyWordRepository
                .findWithImageById(rq.getStudyWordId())
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND))
                .edit(rq.getName(), rq.getMeaning());
    }


    @Override
    public void reorder(StudyWordDto.Reorder rq) {

        // [1] 정렬 대상 및 전체 목록 조회
        StudyWord targetWord =
                studyWordRepository
                        .findById(rq.getStudyWordId())
                        .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // displayOrder 순 정렬된 목록
        List<StudyWord> studyWords = studyWordRepository.findByStudyId(rq.getStudyId());

        // [2] 케이스에 따라 displayOrder 계산 후 갱신
        targetWord.reorder(calculateOrder(targetWord, studyWords, rq.getReorderPosition()));
    }


    // 정렬 시도 후, 재정렬이 필요하면 재정렬 수행 후 다시 정렬
    private Double calculateOrder(StudyWord targetWord, List<StudyWord> studyWords, Integer reorderPosition) {

        // [1] 재정렬 수행
        Double displayOrder = ReorderUtils.calculateReorder(targetWord, studyWords, reorderPosition);

        // [2] 만약 null 반환 시, 일괄 재정렬 후 재시도
        if (Objects.isNull(displayOrder))
            displayOrder = ReorderUtils.rebalanceAndReorder(targetWord, studyWords, reorderPosition);

        // [3] 계산된 재정렬 값 반환
        return displayOrder;
    }


    @Override
    public void remove(Long studyWordId) {

        // [1] 삭제 전 엔티티 조회
        StudyWord studyWord = studyWordRepository
                .findWithImageById(studyWordId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 엔티티 삭제
        studyWord.removeImage(); // 이미지 Soft Delete 처리
        studyWordRepository.deleteById(studyWordId); // 단어 엔티티 삭제
    }


    @Override
    public void removeImage(Long studyWordId) {

        // [1] 엔티티 조회
        StudyWord studyWord = studyWordRepository
                .findWithImageById(studyWordId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 이미지 Soft Delete 처리
        studyWord.removeImage();
    }
}
