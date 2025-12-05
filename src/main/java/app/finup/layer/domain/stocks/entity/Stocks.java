package app.finup.layer.domain.stocks.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "stocks")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Stocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String mkscShrnIscd;     // 단축코드

    @Column(nullable = false)
    private String htsKorIsnm;       // 한글명

    @Builder
    private Stocks(String mkscShrnIscd, String htsKorIsnm) {
        this.mkscShrnIscd = mkscShrnIscd;
        this.htsKorIsnm = htsKorIsnm;
    }
}
