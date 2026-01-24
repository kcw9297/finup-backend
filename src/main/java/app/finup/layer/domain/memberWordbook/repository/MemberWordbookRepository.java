package app.finup.layer.domain.memberWordbook.repository;

import app.finup.layer.domain.memberWordbook.entity.MemberWordbook;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.words.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface MemberWordbookRepository extends JpaRepository<MemberWordbook, Long> {

    Boolean existsByMemberAndWord(Member member, Words word);

    List<MemberWordbook> findByMember(Member member);

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM MemberWordbook mw
        WHERE mw.member = :member AND mw.word = :word
    """)
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
