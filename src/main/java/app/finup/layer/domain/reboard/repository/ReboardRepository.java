package app.finup.layer.domain.reboard.repository;

import app.finup.layer.domain.reboard.entity.Reboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ReboardRepository extends JpaRepository<Reboard, Long> {

    @Query("""
        SELECT r
        FROM Reboard r
        WHERE r.name = :name
    """)
    Optional<Reboard> findByName(@Param("name") String name);


    @Query("""
        SELECT r
        FROM Reboard r
        WHERE r.name = :name AND r.subject = :subject
    """)
    Optional<Reboard> findByNameAndSubject(@Param("name") String name,
                                           @Param("subject") String subject);

    @Query("""
        SELECT r
        FROM Reboard r
        WHERE r.regdate >= :regdate
        ORDER BY r.idx DESC
    """)
    List<Reboard> findByRegdateAfter(@Param("regdate") LocalDateTime regdate);

    @Query("""
        SELECT r
        FROM Reboard r
        WHERE r.idx = :id 
        ORDER BY r.idx DESC 
        LIMIT :lim
    """)
    List<Reboard> findTopNById(@Param("id") Long id,
                               @Param("lim") Integer lim);
}
