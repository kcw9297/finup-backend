package app.finup.layer.domain.notice.entity;


import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "notice")
@DynamicUpdate
@Getter
@ToString(callSuper = true, exclude = "admin")
@EqualsAndHashCode(callSuper = true, exclude = "admin")
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 작성한 관리자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member admin;

    @Builder
    public Notice(String title, String content, Member admin) {
        this.title = title;
        this.content = content;
        this.admin = admin;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
