package app.finup.layer.domain.stock.entity;

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
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String mkscShrnIscd;     // 종목코드

    @Column(nullable = false)
    private String htsKorIsnm;       // 한글명

    @Builder
    private Stock(String mkscShrnIscd, String htsKorIsnm) {
        this.mkscShrnIscd = mkscShrnIscd;
        this.htsKorIsnm = htsKorIsnm;
    }
}
