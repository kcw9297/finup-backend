package app.finup.layer.domain.words.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "words")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Words {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Builder
    public Words(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // 혹시 몰라 일단 업데이트 로직 넣음
    public void updateDescription(String description) {
        this.description = description;
    }
}
