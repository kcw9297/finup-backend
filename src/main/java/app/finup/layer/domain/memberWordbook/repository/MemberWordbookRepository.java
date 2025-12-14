package app.finup.layer.domain.memberWordbook.repository;

import app.finup.layer.domain.memberWordbook.entity.MemberWordbook;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.words.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberWordbookRepository extends JpaRepository<MemberWordbook, Long> {
    Boolean existsByMemberAndWord(Member member, Words word);

    List<MemberWordbook> findByMember(Member member);

    void deleteByMemberAndWord(Member member, Words word);

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
}
