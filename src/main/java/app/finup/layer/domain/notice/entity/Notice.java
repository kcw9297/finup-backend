package app.finup.layer.domain.notice.entity;


import app.finup.layer.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "notice")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
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

    @Column(nullable = false)
    private Long viewCount; // 조회수

    // 생성자
    @Builder
    public Notice(String title, String content) {
        this.title = title;
        this.content = content;
        setDefault();
    }

    // 기본 설정
    private void setDefault() {
        viewCount = 0L;
    }

    /* 변경 감지 메소드 */

    /** 
     * 공지사항 업데이트
     * @param title 제목
     * @param content 내용
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 조회수 업데이트
     * @param viewCount 증가되는 조회수
     */
    public void watch(Long viewCount) {
        this.viewCount += viewCount;
    }


}
