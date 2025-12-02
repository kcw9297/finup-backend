package app.finup.layer.domain.reboard.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.reboard.entity.Reboard;
import app.finup.layer.domain.reboard.dto.ReboardDto;
import app.finup.layer.domain.reboard.dto.ReboardDtoMapper;
import app.finup.layer.domain.reboard.mapper.ReboardMapper;
import app.finup.layer.domain.reboard.repository.ReboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ReboardService 구현 클래스
 * @author kcw
 * @since 2025-11-24
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReboardServiceImpl implements ReboardService {

    private final ReboardRepository reboardRepository;
    private final ReboardMapper reboardMapper;

    @Override
    public Long write(ReboardDto.Write rq) {

        // [1] DTO -> Entity 변환
        Reboard entity = Reboard.builder()
                .name(rq.getName())
                .subject(rq.getSubject())
                .content(rq.getContent())
                .build();

        // [2] 저장 후, 고유번호 반환
        return reboardRepository.save(entity).getIdx();
    }

    @Override
    public void edit(ReboardDto.Edit rq) {

        // 조회 및 갱신
        reboardRepository
                .findById(rq.getIdx())
                .orElseThrow(() -> new BusinessException(AppStatus.REBOARD_NOT_FOUND))
                .update(rq.getName(), rq.getSubject(), rq.getContent());
    }

    @Override
    public void remove(Long idx) {
        reboardRepository.deleteById(idx);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReboardDto.Row> search(ReboardDto.Search rq) {

        // [1] 검색
        List<ReboardDto.Row> rp = reboardMapper.search(rq);
        Integer count = reboardMapper.searchCount(rq);

        // [2] 검색 결과 반환 (페이징 객체 변환)
        return Page.of(rp, count, rq.getPageNum(), rq.getPageSize());
    }


    @Override
    @Transactional(readOnly = true)
    public ReboardDto.Detail getDetail(Long idx) {

        return reboardRepository
                .findById(idx)
                .map(ReboardDtoMapper::toDetail)
                .orElseThrow(() -> new BusinessException(AppStatus.REBOARD_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReboardDto.Row> getList() {

        return reboardRepository
                .findAll(Sort.by(Sort.Direction.DESC, "idx"))
                .stream()
                .map(ReboardDtoMapper::toRow)
                .toList();
    }
}