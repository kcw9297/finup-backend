package app.finup.layer.domain.uploadfile.repository;

import app.finup.layer.domain.uploadfile.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 업로드 파일 엔티티 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-11-26
 */
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    List<UploadFile> findByFileOwnerIsNull();
}
