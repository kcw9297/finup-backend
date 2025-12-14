package app.finup.layer.domain.memberWordbook.repository;

import app.finup.layer.domain.memberWordbook.entity.MemberWordbook;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.words.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberWordbookRepository extends JpaRepository<MemberWordbook, Long> {
    Boolean existsByMemberAndWord(Member member, Words word);

    List<MemberWordbook> findByMember(Member member);

    void deleteByMemberAndWord(Member member, Words word);
}
