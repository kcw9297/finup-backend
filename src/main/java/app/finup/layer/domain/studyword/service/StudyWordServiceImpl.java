package app.finup.layer.domain.studyword.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.LogUtils;
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

    private final StudyWordRepository studyWordRepository;
    private final StudyRepository studyRepository;
    private final UploadFileManager uploadFileManager; // 파일 엔티티 및 주소 제공

    @Override
    @Transactional(readOnly = true)
    public List<StudyWordDto.Row> getListByStudy(Long studyId) {

        return studyWordRepository
                .findByStudyId(studyId)
                .stream()
                .map(studyWord -> StudyWordDtoMapper.toRow(studyWord, uploadFileManager::getFullUrl))
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
                ReorderUtils.DEFAULT_DISPLAY_ORDER :
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
                .map(word -> word.getDisplayOrder() + ReorderUtils.DISPLAY_ORDER_INCREMENT)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));
    }


    @Override
    public void uploadImage(Long studyWordId, MultipartFile file) {

        // [1] 단어 정보 조회
        StudyWord studyWord =
                studyWordRepository
                        .findWithImageById(studyWordId)
                        .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 변경 전 원래 이미지 정보 추출
        UploadFile oldImageFile = studyWord.getWordImageFile();

        // [3] 새롭게 등록하는 파일 엔티티 생성 후, 삽입
        UploadFile newImageFile = uploadFileManager.setEntity(file, studyWordId, FileOwner.STUDY_WORD, FileType.UPLOAD);
        studyWord.uploadImage(newImageFile); // cascade = CascadeType.ALL 옵션으로 인해 자동 저장됨

        // [5] 새롭게 추가된 파일 생성
        uploadFileManager.store(file, newImageFile);

        // [6] 만약 이전 파일이 존재하는 경우, 물리적 파일 삭제 수행
        // 삭제 실패는 치명적이지 않으므로, 로그로 기록
        try {
            if (Objects.nonNull(oldImageFile)) uploadFileManager.remove(oldImageFile);
        } catch (Exception e) {
            LogUtils.showWarn(this.getClass(),
                    "기존 파일 삭제 실패. 신규 파일은 정상 등록 : storePath:%s, message:%s",
                    oldImageFile.getFilePath(), e.getMessage()
            );
        }
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

        // [1] 정렬 대상 조회
        StudyWord targetWord = studyWordRepository
                .findById(rq.getStudyWordId())
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 대상 양 옆의 단어 조회. 받아온 id가 null 인 경우, 조회하지 않음
        StudyWord prevWord = findWordIfExists(rq.getPrevStudyWordId());
        StudyWord nextWord = findWordIfExists(rq.getNextStudyWordId());

        // [3] 케이스에 따라 displayOrder 계산 후 갱신
        Double displayOrder = ReorderUtils.calculateReorder(targetWord, prevWord, nextWord);
        targetWord.reorder(displayOrder);
    }


    // 단어 목록 조회
    private StudyWord findWordIfExists(Long studyWordId) {

        return Objects.isNull(studyWordId) ?
                null :
                studyWordRepository
                        .findById(studyWordId)
                        .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));
    }


    @Override
    public void remove(Long studyWordId) {

        // [1] 삭제 전 엔티티 조회
        StudyWord studyWord = studyWordRepository
                .findWithImageById(studyWordId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 엔티티 삭제
        studyWordRepository.deleteById(studyWordId); // cascade = CascadeType.ALL 옵션으로 파일 엔티티도 함께 삭제

        // [3] 파일 물리적 삭제
        uploadFileManager.remove(studyWord.getWordImageFile());
    }


    @Override
    public void removeImage(Long studyWordId) {

        // [1] 엔티티 조회
        StudyWord studyWord = studyWordRepository
                .findWithImageById(studyWordId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 파일 제거
        studyWord.removeImage();

        // [3] 파일 물리적 삭제
        uploadFileManager.remove(studyWord.getWordImageFile());
    }
}
