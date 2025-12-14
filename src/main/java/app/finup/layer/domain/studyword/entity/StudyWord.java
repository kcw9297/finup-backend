package app.finup.layer.domain.studyword.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 단계별 학습 단어 엔티티 클래스
 * @author kcw
 * @since 2025-12-02
 */

@Entity
@Table(name = "study_word")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class StudyWord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyWordId;

    @Column(nullable = false, unique = true) // 중복 불가
    private String name;

    @Column(nullable = false)
    private String meaning;


    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE} // 이미지 자동 저장/수정 처리
    )
    @JoinColumn(name = "word_image_id")
    private UploadFile wordImageFile; // 단어 이미지


    @Builder
    public StudyWord(String name, String meaning) {
        this.name = name;
        this.meaning = meaning;
    }

    /* 갱신 메소드 */

    /**
     * 단어 이미지 세팅
     * @param wordImageFile 이미지 파일 엔티티
     */
    public void uploadImage(UploadFile wordImageFile) {
        this.wordImageFile = wordImageFile;
    }


    /**
     * 단어 이미지 삭제
     * @return 삭제 처리한 이미지 파일 엔티티
     */
    public UploadFile removeImage() {
        UploadFile file = wordImageFile;
        wordImageFile = null;
        return file; // 삭제한 파일 엔티티 반환
    }


    /**
     * 단어명 수정
     * @param name 단어명
     * @param meaning 단어 뜻
     */
    public void edit(String name, String meaning) {
        this.name = name;
        this.meaning = meaning;
    }
}


