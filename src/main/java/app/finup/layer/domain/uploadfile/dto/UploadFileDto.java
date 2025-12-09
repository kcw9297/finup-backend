package app.finup.layer.domain.uploadfile.dto;

import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 업로드 파일 정보 DTO
 * @author kcw
 * @since 2025-11-26
 */

@NoArgsConstructor
public class UploadFileDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long uploadFileId;
        private String originalName;
        private String storeName;
        private Long size;
        private FileOwner fileOwner;
        private FileType fileType;
        private String filePath;
    }


}
