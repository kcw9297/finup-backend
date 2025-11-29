package app.finup.layer.domain.uploadfile.entity;

import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.uploadfile.enums.FileType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

/*
 * [수정 이력]
 *  ▶ ver 1.0 (2025-10-13) : kcw97 최초 작성
 *  ▶ ver 1.1 (2025-10-22) : kcw97 FileType 추가
 */

/**
 * 파일 엔티티 클래스
 * @version 1.1
 * @author kcw
 * @since 2025-11-26
 */

@Entity
@Table(name = "upload_file")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UploadFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uploadFileId;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, unique = true)
    private String storeName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, updatable = false)
    private Long ownerId; // 소유 주인 엔티티 고유번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private FileOwner fileOwner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private FileType fileType;

    @Column(nullable = false)
    private String filePath;

    @Builder
    public UploadFile(String originalName, String storeName, Long size, Long ownerId, FileOwner fileOwner, FileType fileType, String filePath) {
        this.originalName = originalName;
        this.storeName = storeName;
        this.size = size;
        this.ownerId = ownerId;
        this.fileOwner = fileOwner;
        this.fileType = fileType;
        this.filePath = filePath;
    }

    /**
     * JPA 연관관계 메소드 - 갱신 (파일 덮어쓰기)
     * @param originalName 변경 대상 원래 파일명
     * @param size 변경 대상 파일 사이즈 (Byte)
     */
    public void update(String originalName, Long size) {
        this.originalName = originalName;
        this.size = size;
    }
}
