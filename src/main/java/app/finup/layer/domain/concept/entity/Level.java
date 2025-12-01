package app.finup.layer.domain.concept.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "level")
public class Level {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long levelId;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer orderNumber;

    @Builder
    public Level(String name, String description, Integer orderNumber) {
        this.name = name;
        this.description = description;
        this.orderNumber = orderNumber;
    }
}


