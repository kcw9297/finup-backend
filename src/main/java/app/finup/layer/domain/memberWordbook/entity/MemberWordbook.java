package app.finup.layer.domain.memberWordbook.entity;

import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.memberWordbook.enums.MemorizeStatus;
import app.finup.layer.domain.words.entity.Words;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_wordbook")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MemberWordbook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberWordbookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Words word;

    @CreatedDate
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime memorizedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemorizeStatus memorizeStatus;

    @Builder
    public MemberWordbook(Member member, Words word) {
        this.member = member;
        this.word = word;
        this.memorizeStatus = MemorizeStatus.NONE;
    }

    /**
     * 암기 완료 처리
     */
    public void memorize() {
        this.memorizeStatus = MemorizeStatus.MEMORIZED;
        this.memorizedAt = LocalDateTime.now();
    }

    /**
     * 암기 취소 처리
     */
    public void cancelMemorize() {
        this.memorizeStatus = MemorizeStatus.NONE;
        this.memorizedAt = null;
    }

}
