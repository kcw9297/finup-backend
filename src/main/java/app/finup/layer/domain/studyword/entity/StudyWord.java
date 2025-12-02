package app.finup.layer.domain.studyword.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String meaning;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, // 이미지 자동 저장/수정/삭제 처리
            orphanRemoval = true // null 설정 시 이미지 엔티티 자동 삭제 처리
    )
    @JoinColumn(name = "word_image_id")
    private UploadFile wordImageFile; // 단어 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "study_id", nullable = false, updatable = false)
    private Study study;

    @Builder
    public StudyWord(String name, String meaning, Study study) {
        this.name = name;
        this.meaning = meaning;
        this.study = study;
    }

    /* 연관관계 메소드 */

    public void setImage(UploadFile wordImageFile) {
        this.wordImageFile = wordImageFile;
    }


}


