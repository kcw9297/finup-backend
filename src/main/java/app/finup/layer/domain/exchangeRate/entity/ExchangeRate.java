package app.finup.layer.domain.exchangeRate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rate")
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exchangeRateId;

    @Column(nullable = false, unique = true)
    private String currency; // USD, JPY

    @Column(nullable = false)
    private double dealBasR; // 매매 기준율

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 마지막 업데이트

    @Builder
    private ExchangeRate(String currency, double dealBasR, LocalDateTime updatedAt) {
        this.currency = currency;
        this.dealBasR = dealBasR;
        this.updatedAt = updatedAt;
    }
}