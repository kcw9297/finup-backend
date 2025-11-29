package app.finup.layer.domain.uploadfile.dto;

import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 파일 기본 정보 DTO
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


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Upload implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @JsonIgnore // JSON 직렬화 제외
        private transient MultipartFile file;

        private String originalName;
        private String storeName;
        private Long size;
        private Long entityId;          //  업로드 파일이 속하는 엔티티 고유번호
        private FileOwner fileOwner;    //  업로드 파일이 속하는 엔티티
        private FileType fileType;      // 파일 유형 (썸네일, 직접업로드, 프로필 이미지, 에디터, ...)
        private String filePath;        // 현재 파일이 저장된 경로 (도메인 제외)
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Remove implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long uploadFileId;
        private String filePath;
    }


}
