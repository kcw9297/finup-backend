package app.finup.layer.domain.level.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "level")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Level  {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long levelId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer orderNumber;

    @Builder
    public Level(String name, String description, Integer orderNumber) {
        this.name = name;
        this.description = description;
        this.orderNumber = orderNumber;
    }
    /**
     * 수정용 메서드
     */
    public void edit(String name, String description, Integer orderNumber) {
        this.name = name;
        this.description = description;
        this.orderNumber = orderNumber;
    }
}


