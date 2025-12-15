package app.finup.layer.domain.study.entity;

import app.finup.layer.base.entity.BaseEntity;
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

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private Integer level; // 수준

    @Builder
    public Study(String name, String summary, Integer level) {
        this.summary = summary;
        this.name = name;
        this.level = level;
    }

    /* 갱신 메소드 */

    /**
     * 단계 학습정보 수정
     *
     * @param name    단계 학습명
     * @param summary 요약 내용
     * @param level   학습 레벨
     */
    public void edit(String name, String summary, Integer level) {
        this.name = name;
        this.summary = summary;
        this.level = level;
    }
}


