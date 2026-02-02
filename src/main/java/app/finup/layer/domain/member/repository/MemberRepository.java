package app.finup.layer.domain.member.repository;

import app.finup.common.dto.Page;
import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.enums.MemberSocial;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 회원 상세 조회 전용 (프로필 이미지 Fetch) - id
     */

    @Query("""
        SELECT m
        FROM Member m
        LEFT JOIN FETCH m.profileImageFile
        WHERE m.memberId = :memberId
    """)
    Optional<Member> findByIdWithProfileImage(Long memberId);

    @Query("""
        SELECT m
        FROM Member m
        LEFT JOIN FETCH m.profileImageFile
        WHERE m.email = :email AND m.social = :social
    """)
    Optional<Member> findByEmailAndSocial(String email, MemberSocial social);


    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

}

