package app.finup.layer.domain.studyword.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * 단계별 학습 단어 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyWordService {

    /**
     * 개념 학습 단어 페이징 목록 조회
     * @param rq 페이징 요청 DTO
     */
    Page<StudyWordDto.Row> search(StudyWordDto.Search rq);


    /**
     * 특정 단계 학습정보에 학습 단어 추가
     * @param rq 학습 단어 추가요청 DTO
     */
    void add(StudyWordDto.Add rq);


    /**
     * 학습 단어 내 이미지 업로드 (학습용 이미지)
     * 이미 업로드한 이미지가 있는 경우, 새로운 이미지로 대체
     *
     * @param studyWordId 학습단어번호
     * @param file        업로드 이미지 파일
     * @return
     */
    String uploadImage(Long studyWordId, MultipartFile file);


    /**
     * 학습 단어정보 변경
     * @param rq 변경 요청 DTO
     */
    void edit(StudyWordDto.Edit rq);


    /**
     * 단어 삭제
     * @param studyWordId 학습단어번호
     */
    void remove(Long studyWordId);


    /**
     * 단어 내 이미지 제거
     * @param studyWordId 학습단어번호
     */
    void removeImage(Long studyWordId);
}
