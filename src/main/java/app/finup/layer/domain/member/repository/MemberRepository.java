package app.finup.layer.domain.member.repository;

import app.finup.layer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    @Query("""
        SELECT m
        FROM Member m
        LEFT JOIN FETCH m.profileImage
        WHERE m.email = :email
    """)
    Optional<Member> findByEmail(String email);
}
