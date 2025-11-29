package app.finup.layer.domain.reboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "reboard")
@DynamicUpdate
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Reboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(length = 20)
    private String name;

    @Column(length = 40)
    private String subject;

    @Column(length = 50)
    private String content;

    @CreatedDate
    @LastModifiedDate
    private LocalDateTime regdate;

    @Builder
    public Reboard(String name, String subject, String content) {
        this.name = name;
        this.subject = subject;
        this.content = content;
    }

    public void update(String name, String subject, String content) {
        this.name = name;
        this.subject = subject;
        this.content = content;
    }
}