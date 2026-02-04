package app.finup.layer.domain.uploadfile.manager;

import app.finup.common.utils.EnvUtils;
import app.finup.common.utils.StrUtils;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * UploadFileManager 구현체
 * @author kcw
 * @since 2025-12-04
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadFileManagerImpl implements UploadFileManager {

    private final Environment env; // 프로필 상태 확인
    private final FileStorage fileStorage; // 물리적 파일 조작

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${app.file.domain}")
    private String fileDomain;

    @Value("${app.file.dir}")
    private String fileDir;

    @Override
    public UploadFile setEntity(MultipartFile file, Long ownerId, FileOwner fileOwner, FileType fileType) {

        // 파일 정보 추출
        String originalName = file.getOriginalFilename();
        String ext = extractFileExt(originalName);
        String storeName = "%s.%s".formatted(StrUtils.createUUID(), ext);
        String filePath = "%s/%s".formatted(fileOwner.getValue(), storeName);

        // 업로드 정보 DTO 생성 및 반환
        return UploadFile.builder()
                .originalName(originalName)
                .storeName(storeName)
                .size(file.getSize())
                .ownerId(ownerId)
                .fileOwner(fileOwner)
                .fileType(fileType)
                .filePath(filePath)
                .build();
    }


    @Override
    public String getFullUrl(String filePath) {

        return EnvUtils.isProd(env) ?
                "%s/%s".formatted(fileDomain, filePath) :
                "http://localhost:%s/%s/%s".formatted(serverPort, fileDomain, filePath);
    }


    @Override
    public String store(MultipartFile file, String filePath) {

        // [1] 파일 업로드 경로 조합
        String storePath = getStorePath(filePath);

        // [2] 파일 업로드 수행
        fileStorage.upload(file, storePath);

        // [3] 저장된 file fullUrl 반환 (내부 메소드 사용)
        return getFullUrl(filePath);
    }


    @Override
    public byte[] download(String filePath) {

        // [1] 파일 업로드 경로 조합
        String storePath = getStorePath(filePath);

        // [2] 파일 다운로드 처리를 위한 바이트 스트림 추출 및 반환
        return fileStorage.download(storePath);
    }


    @Override
    public void remove(String filePath) {

        // [1] 파일 업로드 경로 조합
        String storePath = getStorePath(filePath);

        // [2] 파일 삭제
        fileStorage.remove(storePath);
    }


    // 파일 확장자 추출
    private static String extractFileExt(String originalFilename) {
        return Objects.isNull(originalFilename) || originalFilename.isBlank() ?
                "" : originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }


    // 배포/로컬 환경에 따라 업로드 주소 생성 및 반환
    private String getStorePath(String filePath) {

        return EnvUtils.isProd(env) ?
                filePath : // AWS S3는 상대 경로 그대로 사용
                "%s/%s".formatted(fileDir, filePath);
    }

}
