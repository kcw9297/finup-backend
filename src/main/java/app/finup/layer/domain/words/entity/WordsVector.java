package app.finup.layer.domain.words.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "words_vector")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class WordsVector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termId;

    @Column(name = "embedding", columnDefinition = "VECTOR(1536)", nullable = false)
    private byte[] embedding;
}
