package app.finup.infra.file.storage;

import app.finup.common.constant.Env;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Objects;


/**
 * AWS S3 스토리지 파일 조작 기능 제공 FileStorage 구현체
 * @author kcw
 * @since 2025-12-18
 */

@Profile(Env.PROFILE_PROD)
@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    // 사용 의존성
    private final S3Client s3;

    // 사용 상수
    @Value("${app.file.s3.bucket-name}")
    private String bucketName;

    @Value("${app.file.domain}")
    private String fileDomain;


    @Override
    public void upload(MultipartFile file, String filePath) {

        try {
            // [1] 파일 검증
            if (Objects.isNull(file)) {
                LogUtils.showError(this.getClass(), "파일이 서버에 전달되지 않아 업로드 실패!");
                throw new ProviderException(AppStatus.FILE_NOT_EXIST); // 파일이 존재하지 않을 시
            }

            if (file.isEmpty()) {
                LogUtils.showError(this.getClass(), "비어 있는 파일 업로드 시도로 인한 실패!");
                throw new ProviderException(AppStatus.FILE_EMPTY); // 비어 있는 파일 업로드 시
            }

            // [2] S3 업로드 요청 객체 생성
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            // [3] S3 업로드
            s3.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "AWS S3 파일 업로드 실패! 원인 : %s", e.getMessage());
            throw new ProviderException(AppStatus.FILE_UPLOAD_FAILED, e);
        }

    }


    @Override
    public byte[] download(String filePath) {

        try {
            // [1] S3 스토리지에 저장된 파일 다운로드 요청 (InputStream)
            ResponseInputStream<GetObjectResponse> getRes = s3.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(filePath)
                            .build()
            );

            // [2] 파일 다운로드 수행 후, byte 문자열 반환
            return getRes.readAllBytes();

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "AWS S3 파일 다운로드 실패! 원인 : %s", e.getMessage());
            throw new ProviderException(AppStatus.FILE_DOWNLOAD_FAILED, e);
        }
    }


    @Override
    public void remove(String filePath) {

        try {
            s3.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(filePath)
                            .build()
            );

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "AWS S3 파일 삭제 실패! 원인 : %s", e.getMessage());
            throw new ProviderException(AppStatus.FILE_REMOVE_FAILED, e);
        }
    }


    @Override
    public String getUrl(String filePath) {
        return "%s/%s".formatted(fileDomain, filePath);
    }


}
