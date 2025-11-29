package app.finup.layer.domain.uploadfile.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 파일 유형 상수 클래스
 * @author kcw
 * @since 2025-11-26
 */
@Getter
public enum FileType {

    THUMBNAIL("thumbnail"), // 기사 등의 썸네일 이미지 파일
    EDITOR("editor"),       // 텍스트 에디터 내 파일
    PROFILE("profile"),     // 프로필 이미지 파일
    UPLOAD("upload");       // 게시글 등에 직접 업로드한 파일

    private final String value;

    FileType(String value) {this.value = value;}

    public static List<FileType> get() {
        return Arrays.asList(FileType.values());
    }
}
