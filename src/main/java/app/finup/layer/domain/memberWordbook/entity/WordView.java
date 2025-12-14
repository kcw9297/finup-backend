package app.finup.layer.domain.memberWordbook.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/** 로그용 도메인 DB (생성 방식이 조금 다름)
  *  회원이 조회한 단어의 마지막 조회 시점을 관리
  *  조회 기록(log) 성격의 엔티티
  *  연관관계 없이 ID 기반 조회 정렬 수행
  */

@Entity
@Table(name = "member_word_view",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"member_id", "term_id"})
        }
)
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wordViewId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long termId;

    @Column(nullable = false)
    private LocalDateTime lastViewedAt;

    /**
     * 마지막 조회 시간 갱신
     */
    public void refresh() {
        this.lastViewedAt = LocalDateTime.now();
    }
}
