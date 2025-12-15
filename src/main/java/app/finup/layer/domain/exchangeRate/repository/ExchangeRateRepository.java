package app.finup.layer.domain.exchangeRate.repository;

import app.finup.layer.domain.exchangeRate.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByCurrency(String currency);

    List<ExchangeRate> findByCurrencyIn(List<String> currencies);
}