package app.finup.layer.base.validation.validator;

import app.finup.common.utils.StrUtils;
import app.finup.layer.base.utils.ValidationUtils;
import app.finup.layer.base.validation.annotation.ImageFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * 이미지 파일을 검증할 클래스
 * @author kcw
 * @since 2025-12-09
 */

@Slf4j
public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    // 사용자 오류 메세지
    private long maxSizeBytes;
    private boolean nullable;
    private long maxSizeMB;
    private final List<String> ALLOWED_EXTS = List.of("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/webp");

    @Override
    public void initialize(ImageFile annotation) {

        // 기본 파라미터
        maxSizeMB = Math.max(annotation.maxSizeMB(), 10);
        maxSizeBytes = maxSizeMB * 1024 * 1024;  // MB를 Bytes로 변환
        nullable = annotation.nullable();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {

        // null이 가능한 경우, null or empty 통과
        if (nullable && (Objects.isNull(value) || value.isEmpty())) return true;

        // 파일 존재 검증
        if (Objects.isNull(value) || value.isEmpty()) {
            ValidationUtils.addViolation(context, "파일을 선택해 주세요.");
            return false;
        }

        // 파일 크기 검증
        if (value.getSize() > maxSizeBytes) {
            double curMBSize = value.getSize() / 1024.0 / 1024.0;
            String message = "파일 크기는 %dMB 이하여야 합니다 (현재: %.2fMB)".formatted(maxSizeMB, curMBSize);
            ValidationUtils.addViolation(context, message);
            return false;
        }

        // 파일명 검증 (파일명이 없는 경우)
        String originalFilename = value.getOriginalFilename();
        if (Objects.isNull(originalFilename) || originalFilename.isEmpty()) {
            ValidationUtils.addViolation(context, "올바른 파일명을 가진 파일을 업로드 해 주세요.");
            return false;
        }

        // 파일 확장자 검증
        String ext = StrUtils.getFileExt(originalFilename).toLowerCase();
        if (!ALLOWED_EXTS.contains(ext)) {
            String allowedExtsStr = String.join(", ", ALLOWED_EXTS);
            ValidationUtils.addViolation(context, "%s 파일만 업로드할 수 있습니다.".formatted(allowedExtsStr));
            return false;
        }

        // 파일 MIME Type 검증
        String contentType = value.getContentType();
        if (Objects.nonNull(contentType) && !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            ValidationUtils.addViolation(context, "이미지 파일만 업로드할 수 있습니다");
            return false;
        }

        // 검증 성공 시 true 반환
        return true;
    }

}
