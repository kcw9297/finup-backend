package app.finup.layer.domain.studyprogress.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.studyprogress.enums.StudyStatus;
import app.finup.layer.domain.study.entity.Study;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 회원이 진행 중인 학습상태 기록을 위한 엔티티 클래스
 * @author kcw
 * @since 2025-12-02
 */

@Entity
@Table(name = "study_progress")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class StudyProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberStudyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyStatus studyStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id", updatable = false, nullable = false)
    private Member member; // 학습 중인 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "study_id", updatable = false, nullable = false)
    private Study study;

    @Builder
    public StudyProgress(Member member, Study study, StudyStatus studyStatus) {
        this.member = member;
        this.study = study;
        this.studyStatus = studyStatus;
    }

    /* 갱신 메소드 */

    /**
     * 완료 상태로 변경
     */
    public void complete() {
        this.studyStatus = StudyStatus.COMPLETED;
    }
}


