package app.finup.layer.domain.words.service;


import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.infra.words.dto.WordsProviderDto;
import app.finup.infra.words.provider.WordsProvider;
import app.finup.infra.words.provider.KbThinkScraper;
import app.finup.infra.words.redis.RedisRecentSearchStorage;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.dto.WordsDtoMapper;
import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.mapper.WordsMapper;
import app.finup.layer.domain.words.repository.WordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

    private final WordsProvider dictionaryProvider;
    private final WordsRepository wordsRepository;
    private final WordsMapper wordsMapper;
    private final KbThinkScraper kbThinkScraper;
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
    public void refreshTerms() {
        // [0] Provider 초기화 시 사용 금지 로직
        if (isInitialized()) {
            log.warn("금융 용어 사전은 이미 초기화되었습니다. 재실행 불가.");
            throw new ProviderException(AppStatus.FINANCE_DICT_API_FAILED);
        }


        // [1] Provider 호출 → 외부 API에서 name + description 가져옴
        List<WordsProviderDto.Row> rows = dictionaryProvider.fetchTerms();
        log.info("금융 용어 {}건 수집", rows.size());

        // [2] DB 저장 (Upsert) 객체 생성 후 저장
        for (WordsProviderDto.Row row : rows) {
            wordsRepository.save(
                            Words.builder()
                                    .name(row.getName())
                                    .description(row.getDescription())
                                    .build()
            );
        }
        log.info("금융용어 {}건 초기 적재 완료!", rows.size());
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
    @Transactional
    public void crawlAllFromKbThink() {
        Integer page = 1;

        while (true) {
            List<KbThinkScraper.TermSummary> list =
                    kbThinkScraper.fetchList(page);

            if (list.isEmpty()) break;

            for (KbThinkScraper.TermSummary item : list) {

                // 상세 설명
                String detail = kbThinkScraper.fetchDetail(item.getDetailUrl());

                // 중복 확인 후 저장
                wordsRepository.findByName(item.getName())
                        .ifPresentOrElse(
                                entity -> entity.updateDescription(detail),
                                () -> wordsRepository.save(
                                        Words.builder()
                                                .name(item.getName())
                                                .description(detail)
                                                .build()
                                )
                        );
            }
            page++;
        }
    }

    @Override
    public List<String> getRecent(Long memberId) {

        return redisRecentSearchStorage.getRecent(memberId, 10);
    }

    @Override
    public void clear(Long memberId) {
        redisRecentSearchStorage.clear(memberId);
    }
}

