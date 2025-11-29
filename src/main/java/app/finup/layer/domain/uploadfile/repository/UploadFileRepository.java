package app.finup.layer.domain.uploadfile.repository;

import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    @Query("""
        SELECT uf
        FROM UploadFile uf
        WHERE uf.ownerId = :ownerId AND uf.fileOwner = :fileOwner
    """)
    Optional<UploadFile> findByOwnerIdAndFileOwner(Long ownerId, FileOwner fileOwner);

    @Query("""
        SELECT uf
        FROM UploadFile uf
        WHERE uf.ownerId = :ownerId
    """)
    List<UploadFile> findByOwnerId(Long ownerId);

    @Query("""
        SELECT uf
        FROM UploadFile uf
        WHERE uf.ownerId = :ownerId AND uf.fileType = :fileType
    """)
    List<UploadFile> findByOwnerIdAndFileType(Long ownerId, FileType fileType);
}
