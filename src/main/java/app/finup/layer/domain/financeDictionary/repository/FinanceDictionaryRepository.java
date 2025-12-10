package app.finup.layer.domain.financeDictionary.repository;

import app.finup.layer.domain.financeDictionary.entity.FinanceDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FinanceDictionaryRepository extends JpaRepository<FinanceDictionary, Long> {
    Optional<FinanceDictionary> findByName(@Param("name") String name);
}
