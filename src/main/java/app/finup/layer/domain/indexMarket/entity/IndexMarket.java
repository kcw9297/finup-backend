package app.finup.layer.domain.indexMarket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;

@Entity
@Table(name = "index_market")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class IndexMarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long indexMarketId;

    @Column(nullable = false, unique = true)
    private String indexName; // KOSPI, KOSDAQ

    @Column(nullable = false)
    private double closePrice; // 종가

    @Column(nullable = false)
    private double diff; // 전일 대비

    @Column(nullable = false)
    private double rate; // 등락률

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 마지막 업데이트

    @Builder
    private IndexMarket(String indexName, double closePrice, double diff, double rate, LocalDateTime updatedAt) {
        this.indexName = indexName;
        this.closePrice = closePrice;
        this.diff = diff;
        this.rate = rate;
        this.updatedAt = updatedAt;
    }
}