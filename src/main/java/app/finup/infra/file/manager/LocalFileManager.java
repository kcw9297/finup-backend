package app.finup.infra.file.manager;

import app.finup.common.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import app.finup.common.constant.Env;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ManagerException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * 파일의 저장, 삭제, 다운로드 메소드 처리
 * @author kcw
 * @since 2025-11-26
 */

@Profile(Env.PROFILE_LOCAL)
@Slf4j
@Component
public class LocalFileManager implements FileManager {

    @Value("${file.dir}")
    private String fileDir;

    @Value("${file.domain}")
    private String fileDomain;

    @Override
    public String upload(MultipartFile file, String storeName, String entityName) {

        try {

            // [1] 저장 파일 디렉토리 확인 (없을 시 생성)
            String uploadPath = "%s/%s/".formatted(fileDir, entityName);
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();

            // [2] 파일 검증
            if (Objects.isNull(file) || file.isEmpty()) {
                LogUtils.showError(this.getClass(), "파일이 존재하지 않아 업로드 실패!");
                throw new ManagerException(AppStatus.FILE_NOT_FOUND); // 파일이 존재하지 않으면 예외 발생
            }

            // [3] 파일 업로드 & 파일 업로드 정보 DTO 생성 후 반환
            file.transferTo(new File("%s/%s".formatted(uploadPath, storeName))); // 파일 업로드
            return "%s/%s/%s".formatted(fileDomain, entityName, storeName); // 저장한 파일 경로 반환

        } catch (ManagerException e) {
            throw e;

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "로컬 스토리지 파일 업로드에 실패! 오류 : %s", e.getMessage());
            throw new ManagerException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }


    @Override
    public byte[] download(String storeName, String entityName) {

        try {
            // [1] 파일 저장 경로
            String filePath = "%s/%s/%s".formatted(fileDir, entityName, storeName);

            // [2] Path 생성
            Path path = Paths.get(filePath);

            // [3] 파일 바이트 문자열 반환
            return Files.readAllBytes(path);

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "로컬 스토리지 파일 업로드에 실패! 오류 : %s", e.getMessage());
            throw new ManagerException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }


    @Override
    public void remove(String storeName, String entityName) {

        try {
            // [1] 파일 저장 경로
            String filePath = "%s/%s/%s".formatted(fileDir, entityName, storeName);
            Path path = Paths.get(filePath);

            // [2] 파일이 존재하는 경우 파일 삭제
            if (Files.exists(path)) Files.delete(path);

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "로컬 스토리지 파일 업로드에 실패! 오류 : %s", e.getMessage());
            throw new ManagerException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }

    @Override
    public void remove(String filePath) {

        try {
            // [1] 파일 저장 경로
            Path path = Paths.get("%s/%s".formatted(fileDir, filePath));

            // [2] 파일이 존재하는 경우 파일 삭제
            if (Files.exists(path)) Files.delete(path);

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "로컬 스토리지 파일 업로드에 실패! 오류 : %s", e.getMessage());
            throw new ManagerException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }

}
