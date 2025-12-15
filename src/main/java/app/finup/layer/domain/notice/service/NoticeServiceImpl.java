package app.finup.layer.domain.notice.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.notice.dto.NoticeDtoMapper;
import app.finup.layer.domain.notice.entity.Notice;
import app.finup.layer.domain.notice.mapper.NoticeMapper;
import app.finup.layer.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NoticeService 구현 클래스
 * @author khj
 * @since 2025-12-01
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;


    @Override
    @Transactional(readOnly = true)
    public NoticeDto.Detail getDetail(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .map(NoticeDtoMapper::toDetailDto)
                .orElseThrow(() -> new BusinessException(AppStatus.NOTICE_NOT_FOUND));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDto.Row> getPagedList(NoticeDto.Search rq) {

        // [1] 검색 수행
        List<NoticeDto.Row> rows = noticeMapper.search(rq);
        Integer count = noticeMapper.countForSearch(rq);

        // [2] 검색 결과 반환 (페이징 객체 변환)
        return Page.of(rows, count, rq.getPageNum(), rq.getPageSize());
    }


    @Override
    public void write(NoticeDto.Write rq) {

        // [1] DTO -> Entity
        Notice entity = Notice.builder()
                .title(rq.getTitle())
                .content(rq.getContent())
                .build();

        // [2] 공지사항 저장
        noticeRepository.save(entity);
    }


    @Override
    public void edit(NoticeDto.Edit rq) {

        noticeRepository
                .findById(rq.getNoticeId())
                .orElseThrow(() -> new BusinessException(AppStatus.NOTICE_NOT_FOUND))
                .update(rq.getTitle(), rq.getContent());
    }


    @Override
    public void watch(List<NoticeDto.Watch> rq) {
        noticeMapper.updateBulkViewCount(rq);
    }


    @Override
    public void remove(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }


}
