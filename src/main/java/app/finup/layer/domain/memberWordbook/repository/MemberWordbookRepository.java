package app.finup.layer.domain.memberWordbook.repository;

import app.finup.layer.domain.memberWordbook.entity.MemberWordbook;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.words.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberWordbookRepository extends JpaRepository<MemberWordbook, Long> {

    // 내 단어장 단어 존재 여부
    Boolean existsByMemberAndWord(Member member, Words word);

    // 멤버별 단어장 조회
    List<MemberWordbook> findByMember(Member member);

    // 내 단어장에서 단어 삭제
    void deleteByMemberAndWord(Member member, Words word);

    // 내 단어장에서 존재하는지 여부 판단
    @Query("""
                SELECT COUNT(mw) > 0
                FROM MemberWordbook mw
                WHERE mw.member.memberId = :memberId
                  AND mw.word.termId = :termId
            """)
    boolean exists(
            @Param("memberId") Long memberId,
            @Param("termId") Long termId
    );

    // 단어장 암기 항목 조회
    @Query("""
        SELECT mw
        FROM MemberWordbook mw
        WHERE mw.member.memberId = :memberId
          AND mw.word.termId = :termId
    """)
    Optional<MemberWordbook> findByMemberIdAndTermId(
            @Param("memberId") Long memberId,
            @Param("termId") Long termId);
}
