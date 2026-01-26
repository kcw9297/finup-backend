package app.finup.infra.file.storage;

import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import app.finup.common.constant.Env;
import app.finup.common.enums.AppStatus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * 로컬 파일 조작 기능을 제공하는 FileStorage 구현체
 * @author kcw
 * @since 2025-11-26
 */

@Profile(Env.PROFILE_LOCAL)
@Slf4j
@Component
public class LocalFileStorage implements FileStorage {

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${app.file.domain}")
    private String fileDomain;

    @Value("${app.file.dir}")
    private String fileDir;


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

            // [2] 저장 파일 주소 생성 및 파일 디렉토리 생성
            String storePath = getStorePath(filePath);
            String uploadDir = "%s/".formatted(Paths.get(storePath).getParent().toString());
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // [3] 파일 업로드
            file.transferTo(new File(storePath)); // 파일 업로드

        } catch (ProviderException e) {
            throw e;

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "로컬 스토리지 파일 업로드에 실패! 오류 : %s", e.getMessage());
            throw new ProviderException(AppStatus.FILE_UPLOAD_FAILED, e);
        }
    }


    @Override
    public byte[] download(String filePath) {

        try {
            // [1] 파일 저장 경로
            Path path = Paths.get(getStorePath(filePath));

            // [3] 파일 바이트 문자열 반환
            return Files.readAllBytes(path);

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "로컬 스토리지 파일 업로드에 실패! 오류 : %s", e.getMessage());
            throw new ProviderException(AppStatus.FILE_DOWNLOAD_FAILED, e);
        }
    }


    @Override
    public void remove(String filePath) {

        try {
            // [1] 파일 저장 경로
            Path path = Paths.get(getStorePath(filePath));

            // [2] 파일이 존재하는 경우 파일 삭제
            if (Files.exists(path)) Files.deleteIfExists(path);

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "로컬 스토리지 파일 업로드에 실패! 오류 : %s", e.getMessage());
            throw new ProviderException(AppStatus.FILE_DOWNLOAD_FAILED, e);
        }
    }


    @Override
    public String getUrl(String filePath) {
        return "http://localhost:%s/%s/%s".formatted(serverPort, fileDomain, filePath);
    }


    // 실제 "저장"할 주소 반환
    private String getStorePath(String filePath) {
        return "%s/%s".formatted(fileDir, filePath);
    }


}
