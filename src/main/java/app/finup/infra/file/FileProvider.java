package app.finup.infra.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일의 물리적 조작을 처리하는 인터페이스
 * @author kcw
 * @since 2025-11-26
 */
public interface FileProvider {

    /**
     * 파일 업로드
     * @param file      저장 대상 파일
     * @param storePath 저장 주소
     */
    void upload(MultipartFile file, String storePath);


    /**
     * 파일 다운로드 (바이트 문자열 추출)
     * @param storePath 저장 주소
     * @return 파일 바이트 문자열
     */
    byte[] download(String storePath);


    /**
     * 파일 삭제
     * @param storePath 저장 파일 주소
     */
    void remove(String storePath);

}
