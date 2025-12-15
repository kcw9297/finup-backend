package app.finup.layer.domain.exchangeRate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.*;

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
    private double todayRate; // 오늘 환율

    @Column(nullable = false)
    private double yesterdayRate; // 어제 환율

    @Column(nullable = false)
    private LocalDate rateDate; // 기준 날짜

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 마지막 업데이트

    @Builder
    private ExchangeRate(String currency, double todayRate, double yesterdayRate, LocalDate rateDate, LocalDateTime updatedAt) {
        this.currency = currency;
        this.todayRate = todayRate;
        this.yesterdayRate = yesterdayRate;
        this.rateDate = rateDate;
        this.updatedAt = updatedAt;
    }

    public void update(double newRate, LocalDate rateDate) {
        this.yesterdayRate = this.todayRate;
        this.todayRate = newRate;
        this.rateDate = rateDate;
        this.updatedAt = LocalDateTime.now();
    }
}