package app.finup.infra.file.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일의 물리적 조작을 처리하는 인터페이스
 * @author kcw
 * @since 2025-11-26
 */
public interface FileStorage {

    /**
     * 파일 업로드
     * @param file 저장 대상 파일
     * @param filePath 파일 상대 주소
     */
    void upload(MultipartFile file, String filePath);


    /**
     * 파일 다운로드 (바이트 문자열 추출)
     * @param filePath 파일 상대 주소
     * @return 파일 바이트 문자열
     */
    byte[] download(String filePath);


    /**
     * 파일 삭제
     * @param filePath 파일 상대 주소
     */
    void remove(String filePath);


    /**
     * 프론트엔드에 사용할 파일 URL 조회
     * @param filePath 파일 상대 주소
     * @return 실제 프론트엔드에 제공할 파일 URL 문자열
     */
    String getUrl(String filePath);
}
