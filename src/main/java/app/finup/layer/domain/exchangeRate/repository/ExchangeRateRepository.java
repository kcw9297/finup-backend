package app.finup.layer.domain.exchangeRate.repository;

import app.finup.layer.domain.exchangeRate.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    // USD 또는 JPY 조회
    Optional<ExchangeRate> findByCurrency(String currency);
}