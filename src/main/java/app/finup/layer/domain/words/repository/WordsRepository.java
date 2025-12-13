package app.finup.layer.domain.words.repository;

import app.finup.layer.domain.words.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WordsRepository extends JpaRepository<Words, Long> {
    Optional<Words> findByName(@Param("name") String name);
}
