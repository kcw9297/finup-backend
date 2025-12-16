package app.finup.layer.domain.notice.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.LogUtils;
import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.notice.dto.NoticeDtoMapper;
import app.finup.layer.domain.notice.entity.Notice;
import app.finup.layer.domain.notice.mapper.NoticeMapper;
import app.finup.layer.domain.notice.redis.NoticeRedisStorage;
import app.finup.layer.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    // 사용 의존성
    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;
    private final NoticeRedisStorage noticeRedisStorage;

    // 사용 상수
    private static final int AMOUNT_HOME = 3;


    @Override
    @Transactional(readOnly = true)
    public NoticeDto.Detail getDetail(Long noticeId) {

        // [1] 엔티티 조회
        Notice notice = noticeRepository
                .findById(noticeId)
                .orElseThrow(() -> new BusinessException(AppStatus.NOTICE_NOT_FOUND));

        // [2] 조회수 증가 (실패 시에도 통과)
        try {
             noticeRedisStorage.incrementViewCount(noticeId);
        } catch (Exception e) {
            LogUtils.showWarn(this.getClass(), "조회수 증가 실패. 확인 요망. noticeId = %s", noticeId);
        }

        // [3] 조회 결과 반환 (현재 증가치를 더해서 반환)
        return NoticeDtoMapper.toDetail(notice);
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
    @Transactional(readOnly = true)
    public List<NoticeDto.Row> getHomeList() {

        return noticeRepository
                .findLatestN(AMOUNT_HOME)
                .stream()
                .map(NoticeDtoMapper::toRow)
                .toList();
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
    public void syncViewCount() {

        // [1] redis 내 동기화가 필요한 게시글 조회
        Map<Long, Long> increments = noticeRedisStorage.getAllIncrements();

        // [2] 벌크 연산 수행 (빈 컬렉션이면 오류 발생)
        if (!increments.isEmpty()) noticeMapper.updateViewCount(increments);
    }


    @Override
    public void remove(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }


}
