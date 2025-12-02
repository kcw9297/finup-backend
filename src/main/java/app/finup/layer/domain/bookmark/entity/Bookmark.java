package app.finup.layer.domain.bookmark.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.bookmark.enums.BookmarkTarget;
import app.finup.layer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 북마크 엔티티 클래스
 * @author kcw
 * @since 2025-12-02
 */

@Entity
@Table(name = "bookmark")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private BookmarkTarget bookmarkTarget;

    @Column(nullable = false, updatable = false)
    private Long targetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member; // 북마크한 회원

    @Builder
    public Bookmark(BookmarkTarget bookmarkTarget, Long targetId, Member member) {
        this.bookmarkTarget = bookmarkTarget;
        this.targetId = targetId;
        this.member = member;
    }
}


