package app.finup.layer.domain.testquizrecord.entity;

import app.finup.layer.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "test_quiz_record")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TestQuizRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long testQuizRecordId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String choice1;

    @Column(nullable = false)
    private String choice2;

    @Column(nullable = false)
    private String choice3;

    @Column(nullable = false)
    private String choice4;

    @Column(nullable = false)
    private Integer answer;

    @Builder
    public TestQuizRecord(String content, String choice1, String choice2, String choice3, String choice4, Integer answer) {
        this.content = content;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.answer = answer;
    }

    public static TestQuizRecord create(String content, String choice1, String choice2, String choice3, String choice4, Integer answer) {
        return TestQuizRecord.builder()
                .content(content)
                .choice1(choice1)
                .choice2(choice2)
                .choice3(choice3)
                .choice4(choice4)
                .answer(answer)
                .build();
    }
}
