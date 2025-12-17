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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detail; // 관리자만 조작할 수 있는, AI 추천을 위한문장

    @Column(nullable = false)
    private Integer level; // 수준

    @Builder
    public Study(String name, String summary, String detail, Integer level) {
        this.name = name;
        this.summary = summary;
        this.detail = detail;
        this.level = level;
    }

    /* 갱신 메소드 */

    /**
     * 단계 학습정보 수정
     * @param name 단계 학습명
     * @param summary 요약 내용
     * @param detail 학습 상세 (AI가 참고 가능)
     * @param level 학습 레벨
     */
    public void edit(String name, String summary, String detail, Integer level) {
        this.name = name;
        this.summary = summary;
        this.detail = detail;
        this.level = level;
    }
}


