package app.finup.layer.domain.financeDictionary.service;


import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.infra.dictionary.dto.DictionaryProviderDto;
import app.finup.infra.dictionary.provider.DictionaryProvider;
import app.finup.infra.dictionary.provider.KbThinkScraper;
import app.finup.layer.domain.financeDictionary.dto.FinanceDictionaryDto;
import app.finup.layer.domain.financeDictionary.dto.FinanceDictionaryDtoMapper;
import app.finup.layer.domain.financeDictionary.entity.FinanceDictionary;
import app.finup.layer.domain.financeDictionary.repository.FinanceDictionaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FinanceDictionaryServiceImpl implements FinanceDictionaryService {

    private final DictionaryProvider dictionaryProvider;
    private final FinanceDictionaryRepository financeDictionaryRepository;
    private final KbThinkScraper kbThinkScraper;

    @Override
    public void refreshTerms() {
        // [0] Provider 초기화 시 사용 금지 로직
        if (isInitialized()) {
            log.warn("금융 용어 사전은 이미 초기화되었습니다. 재실행 불가.");
            throw new ProviderException(AppStatus.FINANCE_DICT_API_FAILED);
        }


        // [1] Provider 호출 → 외부 API에서 name + description 가져옴
        List<DictionaryProviderDto.Row> rows = dictionaryProvider.fetchTerms();
        log.info("금융 용어 {}건 수집", rows.size());

        // [2] DB 저장 (Upsert) 객체 생성 후 저장
        for (DictionaryProviderDto.Row row : rows) {
            financeDictionaryRepository.save(
                            FinanceDictionary.builder()
                                    .name(row.getName())
                                    .description(row.getDescription())
                                    .build()
            );
        }
        log.info("금융용어 {}건 초기 적재 완료!", rows.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceDictionaryDto.Row> search(FinanceDictionaryDto.Search rq) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isInitialized() {
        return financeDictionaryRepository.count() > 0;
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
                financeDictionaryRepository.findByName(item.getName())
                        .ifPresentOrElse(
                                entity -> entity.updateDescription(detail),
                                () -> financeDictionaryRepository.save(
                                        FinanceDictionary.builder()
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
