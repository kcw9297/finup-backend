package app.finup.layer.domain.uploadfile.manager;

import app.finup.common.utils.EnvUtils;
import app.finup.common.utils.StrUtils;
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

        @Value("${server.port}")
        private Integer serverPort;

        @Value("${file.domain}")
        private String fileDomain;

    @Override
    public UploadFile setEntity(MultipartFile file, Long ownerId, FileOwner fileOwner, FileType fileType) {

        // 파일 정보 추출
        String originalName = file.getOriginalFilename();
        String ext = extractFileExt(originalName);
        String storeName = "%s.%s".formatted(StrUtils.createUUID(), ext);
        String filePath = "/%s/%s".formatted(fileOwner.getName(), storeName);

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


    // 파일 확장자 추출
    private static String extractFileExt(String originalFilename) {
        return Objects.isNull(originalFilename) || originalFilename.isBlank() ?
                "" : originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }


    @Override
    public String getFullUrl(String filePath) {

        return EnvUtils.isProd(env) ?
                "%s%s".formatted(fileDomain, filePath) :
                "http://localhost:%s%s%s".formatted(serverPort, fileDomain, filePath);
    }


    @Override
    public String store(UploadFile uploadFile) {
        return "";
    }

    @Override
    public byte[] download(UploadFile uploadFile) {
        return new byte[0];
    }

    @Override
    public void remove(UploadFile uploadFile) {

    }

}
