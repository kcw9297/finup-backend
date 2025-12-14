package app.finup.layer.domain.stock.repository;

import app.finup.layer.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByMkscShrnIscd(String mkscShrnIscd);
    boolean existsByMkscShrnIscd(String code);
}

