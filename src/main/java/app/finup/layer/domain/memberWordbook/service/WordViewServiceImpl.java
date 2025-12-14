package app.finup.layer.domain.memberWordbook.service;

import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.memberWordbook.dto.WordViewDto;
import app.finup.layer.domain.words.repository.WordsRepository;
import app.finup.layer.domain.memberWordbook.entity.WordView;
import app.finup.layer.domain.memberWordbook.repository.WordViewRepository;
import app.finup.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 최근 본 단어 서비스 구현체
 * @author khj
 * @since 2025-12-14
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WordViewServiceImpl implements WordViewService {

    private final WordViewRepository wordViewRepository;
    private final WordsRepository wordsRepository; // 단어 이름 조회용

    /**
     * 단어 조회 기록
     */
    @Override
    public void record(Long termId) {

        Long memberId = SecurityUtil.getLoginMemberId();

        wordViewRepository.findByMemberAndTerm(memberId, termId)
                .ifPresentOrElse(
                        WordView::refresh,
                        () -> wordViewRepository.save(
                                WordView.builder()
                                        .memberId(memberId)
                                        .termId(termId)
                                        .lastViewedAt(LocalDateTime.now())
                                        .build()
                        )
                );
    }

    /**
     * 내 최근 본 단어 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<WordViewDto.Row> getMyRecentWords() {

        Long memberId = SecurityUtil.getLoginMemberId();

        return wordViewRepository.findRecentRows(memberId);
    }


}
