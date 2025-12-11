package app.finup.layer.domain.financeDictionary.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.financeDictionary.dto.FinanceDictionaryDto;

public interface FinanceDictionaryService {
    void refreshTerms();  // 외부 Provider -> 내부 DB 갱신

    Page<FinanceDictionaryDto.Row> search(FinanceDictionaryDto.Search rq);

    Boolean isInitialized();
}
