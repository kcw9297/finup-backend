package app.finup.layer.domain.notice.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.notice.dto.NoticeDtoMapper;
import app.finup.layer.domain.notice.entity.Notice;
import app.finup.layer.domain.notice.mapper.NoticeMapper;
import app.finup.layer.domain.notice.repository.NoticeRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * NoticeService 구현 클래스
 * ReboardServiceImpl 패턴에 완전히 맞춘 구조
 * @author khj
 * @since 2025-12-01
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository; // 작성자(admin) 조회
    private final NoticeMapper noticeMapper;

    @Override
    @Transactional
    public NoticeDto.Detail write(NoticeDto.Write rq) {
        Long adminId = 1L; // 로그인 정보
        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));
        Notice entity = Notice.builder()
                .title(rq.getTitle())
                .content(rq.getContent())
                .admin(admin)
                .build();

        Notice saved = noticeRepository.save(entity);
        return NoticeDtoMapper.toDetailDto(saved);
    }

    @Override
    @Transactional
    public NoticeDto.Detail edit(NoticeDto.Edit rq) {
        Notice notice = noticeRepository.findById(rq.getNoticeId())
                .orElseThrow(() -> new BusinessException(AppStatus.NOTICE_NOT_FOUND));
        // [1] 수정 로직
        notice.update(rq.getTitle(), rq.getContent());

        return NoticeDtoMapper.toDetailDto(notice);
    }

    @Override
    public void remove(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDto.Detail getDetail(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .map(NoticeDtoMapper::toDetailDto)
                .orElseThrow(() -> new BusinessException(AppStatus.NOTICE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDto.Row> getList(NoticeDto.Row rq) {
        List<Notice> list = noticeRepository.findAll(Sort.by(Sort.Direction.ASC, "noticeId"));

        List<NoticeDto.Row> dto =
                list.stream().map(NoticeDtoMapper::toListDto).toList();

        return Page.of(dto, dto.size(), 0, dto.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDto.Row> search(NoticeDto.Search rq) {
        List<NoticeDto.Row> list = noticeMapper.search(rq);
        Long count = noticeMapper.countForSearch(rq);
        // count.intValue() 변환하여 전달
        return Page.of(list, count.intValue(), rq.getPageNum(), rq.getPageSize());
    }
}
