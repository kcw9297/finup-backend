package app.finup.layer.domain.testquiz.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "test_quiz")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TestQuiz extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testQuizId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int score;

    @Builder
    private TestQuiz(Member member, int score) {
        this.member = member;
        this.score = score;
    }

    private static TestQuiz createTestQuiz(Member member, int score) {
        return TestQuiz.builder()
                .member(member)
                .score(score)
                .build();
    }
}