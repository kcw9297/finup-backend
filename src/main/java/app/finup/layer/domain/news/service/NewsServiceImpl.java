package app.finup.layer.domain.news.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.dto.NewsDtoMapper;
import app.finup.layer.domain.news.entity.News;
import app.finup.layer.domain.news.enums.NewsType;
import app.finup.layer.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * NewsService 구현 클래스
 * @author kcw
 * @since 2025-12-24
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {

    // 사용 의존성
    private final NewsRepository newsRepository;

    // 사용 상수
    private static final int PAGE_SIZE = 10;


    @Cacheable(
            value = NewsRedisKey.CACHE_MAIN,
            key = "#pageNum",
            unless = "#result.rows.isEmpty()" // 결과가 비어있는 경우는 캐싱 중단
    )
    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto.Row> getPagedMainNewsList(int pageNum, int pageSize) {

        return doPaging(
                pageNum, pageSize,
                pageable -> newsRepository.findByNewsTypeWithPaging(NewsType.MAIN, pageable), // 페이징 쿼리
                () -> newsRepository.countByNewsType(NewsType.MAIN) // 카운팅 쿼리
        );
    }


    @Cacheable( // 캐싱 등록
            value = NewsRedisKey.CACHE_STOCK,
            key = "#stockCode + ':' + #pageNum",
            unless = "#result.rows.isEmpty()"  // 결과가 비어있는 경우는 캐싱 중단
    )
    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto.Row> getPagedStockNewsList(String stockCode, int pageNum, int pageSize) {

        return doPaging(
                pageNum, pageSize,
                pageable -> newsRepository.findByStockCodeWithPaging(stockCode, pageable), // 페이징 쿼리
                () -> newsRepository.countByStockCode(stockCode) // 카운팅 쿼리
        );
    }


    // 페이징 수행
    private Page<NewsDto.Row> doPaging(
            int pageNum, int pageSize,
            Function<Pageable, List<News>> pagingMethod,
            Supplier<Long> countingMethod) {

        // [1] Pageable 생성
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        // [2] 페이징 쿼리 수행
        List<NewsDto.Row> rows = pagingMethod.apply(pageable)
                .stream()
                .map(NewsDtoMapper::toRow)
                .toList();

        // [3] 카운팅 쿼리 수행
        Long count = countingMethod.get();

        // [4] 페이징 결과 반환
        return Page.of(rows, count, pageNum, pageSize, PAGE_SIZE);
    }

}
