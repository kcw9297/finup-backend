package app.finup.layer.domain.studyword.service;

import app.finup.layer.domain.studyword.dto.StudyWordDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 단계별 학습 단어 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyWordService {

    /**
     * 단계별 학습 개념에 속하는 단어 리스트 조회
     * @param studyId 페이징 요청 DTO
     */
    List<StudyWordDto.Row> getListByStudy(Long studyId);

    void add(StudyWordDto.Add rq);

    void addImage(Long studyWordId, MultipartFile file);

    void edit(StudyWordDto.Edit rq);

    void editImage(Long studyWordId, MultipartFile file);

    void reorder(StudyWordDto.Reorder rq);

    void remove(Long studyWordId);

    void removeImage(Long studyWordId);
}
