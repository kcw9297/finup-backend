package app.finup.layer.domain.uploadfile.service;

import app.finup.layer.domain.uploadfile.dto.UploadFileDto;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;


/**
 * 파일 스케줄러 로직 처리
 * @author kcw
 * @since 2026-01-18
 */
public interface UploadFileSchedulerService {

    /**
     * 고아 상태가 된 파일 삭제
     */
    void clearOrphan();

}
