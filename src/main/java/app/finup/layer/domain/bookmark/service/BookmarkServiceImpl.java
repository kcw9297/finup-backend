package app.finup.layer.domain.bookmark.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.bookmark.dto.BookmarkDto;
import app.finup.layer.domain.bookmark.dto.BookmarkDtoMapper;
import app.finup.layer.domain.bookmark.entity.Bookmark;
import app.finup.layer.domain.bookmark.enums.BookmarkTarget;
import app.finup.layer.domain.bookmark.repository.BookmarkRepository;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * BookmarkService 구현 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookmarkDto.Row> getMyList(Long memberId) {

        return bookmarkRepository
                .findByMemberId(memberId)
                .stream()
                .map(BookmarkDtoMapper::toRow)
                .toList();
    }


    @Override
    public void add(BookmarkDto.Add rq) {

        // [1] 필요한 Entity 조회
        Member member = memberRepository
                .findById(rq.getMemberId())
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));

        // [2] Entity 생성
        Bookmark bookmark = Bookmark.builder()
                .bookmarkTarget(rq.getBookmarkTarget())
                .targetId(rq.getTargetId())
                .member(member)
                .build();

        // [3] Entity 저장
        bookmarkRepository.save(bookmark);
    }


    @Override
    public void remove(Long memberId, Long targetId, BookmarkTarget bookmarkTarget) {
        bookmarkRepository.deleteBy(memberId, targetId, bookmarkTarget);
    }
}
