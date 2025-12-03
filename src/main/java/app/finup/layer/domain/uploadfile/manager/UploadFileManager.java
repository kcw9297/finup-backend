package app.finup.layer.domain.uploadfile.manager;

import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

/**
 * 물리적 파일 관리 및 기타 유틸 기능 제공 인터페이스
 * @author kcw
 * @since 2025-12-04
 */
public interface UploadFileManager {

    /**
     * 업로드 파일 엔티티 클래스 세팅
     * @param file MultipartFile (업로드 파일)
     * @param ownerId 파일 소유자 고유번호
     * @param fileOwner 파일 소유자 정보
     * @param fileType 파일 타입
     * @return 업로드 파일 엔티티 클래스
     */
    UploadFile setEntity(MultipartFile file, Long ownerId, FileOwner fileOwner, FileType fileType);


    /**
     * 현재 사용하는 파일 저장소 도메인을 포함한 Full FileUrl 제공
     * @param filePath 파일 저장 주소 (파일 도메인이 없는 주소)
     * @return 파일 도메인을 포함한 파일 주소
     */
    String getFullUrl(String filePath);


    /**
     * 물리적 파일 저장
     * @param uploadFile 대상 파일 엔티티 클래스
     * @return 저장에 성공한 파일 fullUrl
     */
    String store(UploadFile uploadFile);


    /**
     * 파일 다운로드
     * @param uploadFile 대상 파일 엔티티 클래스
     * @return 읽은 파일 바이트 배열
     */
    byte[] download(UploadFile uploadFile);


    /**
     * 파일 물리적 삭제
     * @param uploadFile 대상 파일 엔티티 클래스
     */
    void remove(UploadFile uploadFile);
}
