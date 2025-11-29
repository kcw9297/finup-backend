package app.finup.layer.domain.uploadfile.dto;

import app.finup.layer.domain.uploadfile.entity.UploadFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadFileDtoMapper {

    public static UploadFileDto.Detail toDetail(UploadFile entity) {
        return UploadFileDto.Detail.builder()
                .uploadFileId(entity.getUploadFileId())
                .originalName(entity.getOriginalName())
                .storeName(entity.getStoreName())
                .size(entity.getSize())
                .fileOwner(entity.getFileOwner())
                .fileType(entity.getFileType())
                .filePath(entity.getFilePath())
                .build();
    }
}
