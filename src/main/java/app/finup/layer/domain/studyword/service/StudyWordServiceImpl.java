package app.finup.layer.domain.studyword.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import app.finup.layer.domain.studyword.dto.StudyWordDtoMapper;
import app.finup.layer.domain.studyword.entity.StudyWord;
import app.finup.layer.domain.studyword.mapper.StudyWordMapper;
import app.finup.layer.domain.studyword.repository.StudyWordRepository;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import app.finup.layer.domain.uploadfile.manager.UploadFileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
    private final StudyWordMapper studyWordMapper;
    private final UploadFileManager uploadFileManager; // 파일 엔티티 및 주소 제공

    @Override
    @Transactional(readOnly = true)
    public Page<StudyWordDto.Row> search(StudyWordDto.Search rq) {

        // [1] 검색
        List<StudyWordDto.Row> rows = studyWordMapper.search(rq);
        Integer count = studyWordMapper.countForSearch(rq);

        // [2] filePath 상대주소에, 현재 프로젝트 파일 도메인 첨부
        rows.stream()
                .filter(row -> Objects.nonNull(row.getImageUrl()))
                .forEach(row -> row.setImageUrl(uploadFileManager.getFullUrl(row.getImageUrl())));

        // [3] 검색 결과 반환 (페이징 객체로 변환)
        return Page.of(rows, count, rq.getPageNum(), rq.getPageSize());
    }


    @Override
    public void add(StudyWordDto.Add rq) {

        // [1] 이미 중복단어가 존재하면 예외 반환
        if (studyWordRepository.existsByName(rq.getName()))
            throw new BusinessException(AppStatus.STUDY_WORD_ALREADY_EXIST, "name");

        // [2] 엔티티 생성
        StudyWord studyWord = StudyWord.builder()
                .name(rq.getName())
                .meaning(rq.getMeaning())
                .build();

        // [3] 엔티티 저장
        studyWordRepository.save(studyWord);
    }


    @Override
    public String uploadImage(Long studyWordId, MultipartFile file) {

        // [1] 단어 정보 조회
        StudyWord studyWord =
                studyWordRepository
                        .findWithImageById(studyWordId)
                        .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 변경 전 원래 이미지 정보 추출 후, 소유자를 null로 변경
        // Soft Delete 처리 (나중에 스케줄러에서 파일 삭제)
        if (Objects.nonNull(studyWord.getWordImageFile())) studyWord.removeImage().softRemove();

        // [3] 새롭게 등록하는 파일 엔티티 생성 후, 삽입
        UploadFile newImageFile = uploadFileManager.setEntity(file, studyWordId, FileOwner.STUDY_WORD, FileType.UPLOAD);
        studyWord.uploadImage(newImageFile); // cascade 옵션으로 인해 자동 저장됨

        // [4] 새롭게 추가된 파일 생성
        uploadFileManager.store(file, newImageFile.getFilePath());

        // [5] 업로드한 파일 주소 반환
        return uploadFileManager.getFullUrl(newImageFile.getFilePath());
    }


    @Override
    public void edit(StudyWordDto.Edit rq) {

        // [1] 변경 대상 단어 조회
        StudyWord studyWord = studyWordRepository
                .findWithImageById(rq.getStudyWordId())
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 만약 "단어명"을 변경하는 경우 중복 검증
        if (!Objects.equals(studyWord.getName(), rq.getName()) &&
                studyWordRepository.existsByName(rq.getName()))
            throw new BusinessException(AppStatus.STUDY_WORD_ALREADY_EXIST, "name");

        // [3] 단어명 갱신 수행
        studyWord.edit(rq.getName(), rq.getMeaning());
    }


    @Override
    public void remove(Long studyWordId) {

        // [1] 삭제 전 엔티티 조회
        StudyWord studyWord = studyWordRepository
                .findWithImageById(studyWordId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 단어 내 이미지 삭제 (존재 시)
        if (Objects.nonNull(studyWord.getWordImageFile())) studyWord.removeImage().softRemove();

        // [3] 단어 삭제
        studyWordRepository.deleteById(studyWordId);
    }


    @Override
    public void removeImage(Long studyWordId) {

        // [1] 엔티티 조회
        StudyWord studyWord = studyWordRepository
                .findWithImageById(studyWordId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));

        // [2] 이미지 존재 여부 확인
        if (Objects.isNull(studyWord.getWordImageFile()))
            throw new BusinessException(AppStatus.STUDY_WORD_IMAGE_NOT_FOUND);

        // [3] 이미지 Soft Delete 처리 및 연관관계 해제
        studyWord.removeImage().softRemove();
    }

    @Override
    public List<StudyWordDto.Quiz> getQuizData() {
        List<StudyWord> studyWordList = studyWordRepository.findRandomWords(PageRequest.of(0, 30));

        List<StudyWordDto.Quiz> result = new ArrayList<>();

        for (StudyWord studyWord : studyWordList) {
            StudyWordDto.Quiz quizData = StudyWordDtoMapper.toQuiz(studyWord);
            result.add(quizData);
        }

        return result;
    }
}
