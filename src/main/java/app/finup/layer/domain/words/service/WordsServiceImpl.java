package app.finup.layer.domain.words.service;


import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.infra.words.dto.WordsProviderDto;
import app.finup.infra.words.provider.WordsProvider;
import app.finup.infra.words.provider.KbThinkScraper;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.mapper.WordsMapper;
import app.finup.layer.domain.words.repository.WordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WordsServiceImpl implements WordsService {

    private final WordsProvider dictionaryProvider;
    private final WordsRepository wordsRepository;
    private final WordsMapper wordsMapper;
    private final KbThinkScraper kbThinkScraper;

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
    public Page<WordsDto.Row> search(WordsDto.Search rq) {

        // 키워드 없을 때도 빈 페이지 반환
        if (rq == null || !StringUtils.hasText(rq.getKeyword())) {
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
}
