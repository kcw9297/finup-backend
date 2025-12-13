package app.finup.layer.domain.words.repository;

import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordsRepository extends JpaRepository<Words, Long> {
    Optional<Words> findByName(@Param("name") String name);

    long count(); // 전체 건수 (isInitialized 용)
}
