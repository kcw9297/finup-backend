package app.finup.infra.file.manager;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일의 저장, 삭제, 다운로드 메소드 처리
 * @author kcw
 * @since 2025-11-26
 */
public interface FileManager {

    /**
     * 파일 업로드
     * @param file       저장 대상 파일
     * @param storeName  저장 파일명
     * @param entityName 저장된 파일이 속하는 엔티티 타입명
     * @return 저장된 파일 경로
     */
    String upload(MultipartFile file, String storeName, String entityName);

    /**
     * 파일 다운로드
     *
     * @param storeName  저장 파일명
     * @param entityName 저장된 파일이 속하는 엔티티 타입명
     * @return 파일 바이트 문자열 반환
     */
    byte[] download(String storeName, String entityName);


    /**
     * 파일 삭제
     * @param storeName  저장 파일명
     * @param entityName 저장된 파일이 속하는 엔티티 타입명
     */
    void remove(String storeName, String entityName);

    /**
     * 파일 삭제
     * @param filePath 저장 파일 주소
     */
    void remove(String filePath);

}
