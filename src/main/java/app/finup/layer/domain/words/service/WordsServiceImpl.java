package app.finup.layer.domain.words.service;


import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.words.redis.RedisRecentSearchStorage;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.dto.WordsDtoMapper;
import app.finup.layer.domain.words.mapper.WordsMapper;
import app.finup.layer.domain.words.repository.WordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WordsServiceImpl implements WordsService {

    private final WordsRepository wordsRepository;
    private final WordsMapper wordsMapper;
    private final RedisRecentSearchStorage redisRecentSearchStorage;

    /**
     * 홈 - 오늘의 단어 (JPA + 랜덤 Offset)
     */
    @Override
    @Transactional(readOnly = true)
    public List<WordsDto.Row> getHomeWords() {

        Long total = wordsRepository.count();
        Integer size = 3;

        if (total == 0) {
            return Collections.emptyList();
        }

        Set<Long> randomIds = new HashSet<>();

        while (randomIds.size() < size) {
            Long randomId = ThreadLocalRandom.current()
                    .nextLong(1, total + 1);
            randomIds.add(randomId);
        }

        return wordsRepository.findAllById(randomIds)
                .stream()
                .map(WordsDtoMapper::toRow)
                .toList();
    }



    @Override
    @Transactional(readOnly = true)
    public Page<WordsDto.Row> search(WordsDto.Search rq, Long memberId) {

        log.info(
                "[RECENT SEARCH] memberId={}, keyword={}",
                memberId,
                rq.getKeyword()
        );

        // [1] 최근 검색어 저장 (로그인 사용자만)
        if (memberId != null && StringUtils.hasText(rq.getKeyword())) {
            redisRecentSearchStorage.add(memberId, rq.getKeyword());
        }

        // 키워드 없을 때도 빈 페이지 반환
        if (!StringUtils.hasText(rq.getKeyword())) {
            return Page.of(
                    Collections.emptyList(),
                    0,
                    rq.getPageNum(),
                    rq.getPageSize()
            );
        }

        List<WordsDto.Row> rows = wordsMapper.search(rq);
        Integer totalCount = wordsMapper.countBySearch(rq);

        return Page.of(rows, totalCount, rq.getPageNum(), rq.getPageSize());
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isInitialized() {
        return wordsRepository.count() > 0;
    }



    @Override
    public List<String> getRecent(Long memberId) {

        return redisRecentSearchStorage.getRecent(memberId, 10);
    }

    @Override
    public void clear(Long memberId) {
        redisRecentSearchStorage.clear(memberId);
    }

    @Override
    public void removeRecent(Long memberId, String keyword) {
        redisRecentSearchStorage.remove(memberId, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public WordsDto.Row getDetail(Long termId) {

        return wordsRepository.findById(termId)
                .map(WordsDtoMapper::toRow)
                .orElseThrow(() ->
                        new BusinessException(AppStatus.WORDS_NOT_FOUND)
                );
    }
}

