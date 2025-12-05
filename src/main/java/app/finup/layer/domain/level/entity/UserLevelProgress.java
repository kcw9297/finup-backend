package app.finup.layer.domain.level.entity;

import app.finup.layer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


import java.time.LocalDateTime;

@Entity
@Table(name = "user_level_progress")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLevelProgress {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    @Column(nullable = false)
    private Integer progress; // 진도율

    @Column
    private LocalDateTime completedAt; //완료시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public UserLevelProgress(Integer progress, LocalDateTime completedAt, Level level, Member member) {
        this.progress = progress;
        this.completedAt = completedAt;
        this.level = level;
        this.member = member;
    }

    // 진행률 갱신용 메소드
    public void updateProgress(Integer progress, LocalDateTime completedAt) {
        this.progress = progress;
        this.completedAt = completedAt;
    }
}

