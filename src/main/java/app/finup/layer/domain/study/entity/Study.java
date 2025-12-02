package app.finup.layer.domain.study.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.study.enums.StudyStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "study")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer level; // 수준

    @Column(nullable = false)
    private StudyStatus studyStatus; // 학습 상태

    @Builder
    public Study(String name, String description, Integer level) {
        this.name = name;
        this.description = description;
        this.level = level;
        setDefault();
    }

    // 기본 값 초기화
    private void setDefault() {
        this.studyStatus = StudyStatus.BEFORE;
    }
}


