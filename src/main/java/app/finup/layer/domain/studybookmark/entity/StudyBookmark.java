package app.finup.layer.domain.studybookmark.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 단계별 학습 북마크 엔티티 클래스
 * @author kcw
 * @since 2025-12-02
 */

@Entity
@Table(name = "study_bookmark")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class StudyBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long levelBookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member; // 북마크한 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "study_id", updatable = false)
    private Study study; // 북마크한 회원

    @Builder
    public StudyBookmark(Member member, Study study) {
        this.member = member;
        this.study = study;
    }
}


