package app.finup.layer.domain.words.entity;

import app.finup.layer.domain.words.enums.WordsLevel;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WordsLevel wordsLevel;

    @Lob // 임베딩 벡터 배열
    @Column(columnDefinition = "VECTOR(1536)", nullable = false)
    private byte[] embedding;

    @Builder
    public Words(String name, String description, WordsLevel wordsLevel, byte[] embedding) {
        this.name = name;
        this.description = description;
        this.wordsLevel = wordsLevel;
        this.embedding = embedding;
    }

}
