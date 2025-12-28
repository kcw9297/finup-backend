package app.finup.layer.domain.memberWordbook.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.memberWordbook.dto.MemberWordbookDto;
import app.finup.layer.domain.memberWordbook.dto.MemberWordbookDtoMapper;
import app.finup.layer.domain.memberWordbook.entity.MemberWordbook;
import app.finup.layer.domain.memberWordbook.repository.MemberWordbookRepository;
import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.repository.WordsRepository;
import app.finup.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberWordbookServiceImpl implements MemberWordbookService {

    private final MemberRepository memberRepository;
    private final WordsRepository wordsRepository;
    private final MemberWordbookRepository memberWordbookRepository;

    @Override
    public List<MemberWordbookDto.Row> getMyWordbook() {
        // [1] 로그인 회원 조회
        Member member = getLoginMember();

        // [2] 단어장 조회
        List<MemberWordbook> wordbooks =
                memberWordbookRepository.findByMember(member);

        // [3] Entity → DTO 매핑
        return wordbooks.stream()
                .map(MemberWordbookDtoMapper::toRow)
                .toList();
    }

    @Override
    public boolean isAdded(Long termId) {
        Long memberId = SecurityUtil.getLoginMemberId();

        return memberWordbookRepository.exists(memberId, termId);
    }

    @Override
    public void add(MemberWordbookDto.Add rq) {
        // [1] 로그인 회원 조회
        Member member = getLoginMember();

        // [2] 단어 조회
        Words word = wordsRepository.findById(rq.getTermId())
                .orElseThrow(() -> new BusinessException(AppStatus.WORD_NOT_FOUND));

        // [3] 중복 담기 방지
        if (memberWordbookRepository.existsByMemberAndWord(member, word)) {
            throw new BusinessException(AppStatus.MEMBER_WORDBOOK_ALREADY_EXISTS);
        }

        // [4] 단어장 저장
        memberWordbookRepository.save(
                MemberWordbook.builder()
                        .member(member)
                        .word(word)
                        .build()
        );
    }

    @Override
    public void remove(Long termId) {
        // [1] 로그인 회원 조회
        Member member = getLoginMember();

        // [2] 단어 조회
        Words word = wordsRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(AppStatus.WORD_NOT_FOUND));

        // [3] 단어장 삭제
        memberWordbookRepository.deleteByMemberAndWord(member, word);
    }

    /**
     * 로그인 회원 조회 (공통)
     */
    private Member getLoginMember() {

        Long memberId = SecurityUtil.getLoginMemberId();

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));
    }

    @Override
    public MemberWordbookDto.MemorizedWord memorize(Long termId, MemberWordbookDto.Memorize rq) {
        // [1] 로그인 사용자 조회
        Long memberId = SecurityUtil.getLoginMemberId();

        // [2] 단어장 엔티티 조회
        MemberWordbook wordbook = memberWordbookRepository.findByMemberIdAndTermId(memberId, termId)
                .orElseThrow(() -> new BusinessException(AppStatus.WORD_NOT_FOUND));

        // [3] 암기 상태 변경
        if (rq.isMemorized()) {
            wordbook.memorize();
        } else {
            wordbook.cancelMemorize();
        }

        // [4] 변경 결과 반환
        return MemberWordbookDto.MemorizedWord.builder()
                .termId(termId)
                .memorizedAt(wordbook.getMemorizedAt())
                .memorizeStatus(wordbook.getMemorizeStatus())
                .build();

    }
}
