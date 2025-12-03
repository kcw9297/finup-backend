package app.finup.layer.domain.study.repository;

import app.finup.layer.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 단계별 학습 엔티티 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyRepository extends JpaRepository<Study, Long> {

}
