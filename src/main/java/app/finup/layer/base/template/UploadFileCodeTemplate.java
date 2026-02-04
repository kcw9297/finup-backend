package app.finup.layer.base.template;

import app.finup.common.utils.StrUtils;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * UploadFile Entity 로직 중, 공용 코드를 제공하는 탬플릿 클래스
 * @author kcw
 * @since 2026-01-17
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadFileCodeTemplate {

    public static String uploadFileAndSaveEntity(
            FileStorage fileStorage,
            MultipartFile file, Long ownerId, FileOwner fileOwner, FileType fileType,
            Consumer<UploadFile> entitySaveFileMethod
    ) {

        // [1] 새롭게 업로드한 file 기반 엔티티 클래스 생성 후, 속하는 엔티티에 저장
        UploadFile newUploadFile = setEntity(file, ownerId, fileOwner, fileType);
        entitySaveFileMethod.accept(newUploadFile);

        // [2] 새롭게 업로드한 파일의 물리적 저장 수행
        fileStorage.upload(file, newUploadFile.getFilePath());

        // [3] 업로드애 성공한 파일의 Url 반환 (프론트에 제공할 FileUrl)
        return fileStorage.getUrl(newUploadFile.getFilePath());
    }



    private static UploadFile setEntity(MultipartFile file, Long ownerId, FileOwner fileOwner, FileType fileType) {

        // 파일 정보 추출
        String originalName = file.getOriginalFilename();
        String ext = StrUtils.getFileExt(originalName);
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

}
