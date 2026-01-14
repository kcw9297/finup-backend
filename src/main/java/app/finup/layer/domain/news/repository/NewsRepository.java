package app.finup.layer.domain.news.repository;

import app.finup.layer.domain.news.entity.News;
import app.finup.layer.domain.news.enums.NewsType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("""
        SELECT n
        FROM News n
        WHERE n.newsType = :newsType
    """)
    List<News> findByNewsType(NewsType newsType);


    @Query("""
        SELECT n
        FROM News n
        WHERE n.newsType = :newsType
        ORDER BY n.publishedAt DESC
    """)
    List<News> findByNewsTypeWithPaging(NewsType newsType, Pageable pageable);


    @Query("""
        SELECT COUNT(n)
        FROM News n
        WHERE n.newsType = :newsType
    """)
    long countByNewsType(NewsType newsType);


    @Query("""
        SELECT n
        FROM News n
        WHERE n.stockCode = :stockCode
        ORDER BY n.publishedAt DESC
    """)
    List<News> findByStockCodeWithPaging(String stockCode, Pageable pageable);


    @Query("""
        SELECT COUNT(n)
        FROM News n
        WHERE n.stockCode = :stockCode
    """)
    long countByStockCode(String stockCode);


    @Modifying
    @Query("""
        DELETE FROM News n
        WHERE n.publishedAt < :thresholdTime
    """)
    void removeOld(LocalDateTime thresholdTime);
}
