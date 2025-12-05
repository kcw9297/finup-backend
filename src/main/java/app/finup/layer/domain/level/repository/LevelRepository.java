package app.finup.layer.domain.level.repository;

import app.finup.layer.domain.level.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Level 엔티티 Repository 인터페이스
 * @author sjs
 * @since 2025-12-05
 */
public interface LevelRepository extends JpaRepository<Level, Long> {

    /**
     * 전체 레벨을 단계순 (1~10)으로 조회
     */
    List<Level> findAllByOrderByOrderNumberAsc();
}
