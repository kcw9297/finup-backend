package app.finup.layer.domain.uploadfile.service;

import app.finup.layer.domain.uploadfile.dto.UploadFileDto;

import java.util.Collection;
import java.util.List;


/**
 * 파일 핵심 비즈니스 로직 처리
 * @author kcw
 * @since 2025-11-26
 */
public interface UploadFileService {

    /**
     * 단일 파일정보 조회
     * @param uploadFileId 대상 파일번호 (PK)
     * @return 조회된 파일 정보 반환
     */
    UploadFileDto.Detail getDetail(Long uploadFileId);


    /**
     * 고아 파일 일괄 조회 (주인 없는 파일)
     * @return 조회된 모든 고아파일 엔티티 리스트
     */
    List<UploadFileDto.Detail> getOrphanList();


    /**
     * 파일 일괄 삭제
     * @param uploadFileIds 삭제 대상 파일번호 리스트
     */
    void removeAll(Collection<Long> uploadFileIds);
}
