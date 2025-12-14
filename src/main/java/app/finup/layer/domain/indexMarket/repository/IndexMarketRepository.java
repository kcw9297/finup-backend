package app.finup.layer.domain.indexMarket.repository;

import app.finup.layer.domain.indexMarket.entity.IndexMarket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IndexMarketRepository extends JpaRepository<IndexMarket, Long> {
    // 최신 지수
    Optional<IndexMarket> findTopByIndexNameOrderByUpdatedAtDesc(String indexName);

    // 이전 지수
    Optional<IndexMarket> findFirstByIndexNameAndUpdatedAtLessThanOrderByUpdatedAtDesc(String indexName, LocalDateTime updatedAt);
}