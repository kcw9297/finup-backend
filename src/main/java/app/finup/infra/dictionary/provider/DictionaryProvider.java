package app.finup.infra.dictionary.provider;


import app.finup.infra.dictionary.dto.DictionaryProviderDto;
import app.finup.layer.domain.financeDictionary.dto.FinanceDictionaryDto;

import java.util.List;

/**
 * 한국예탁결제원 API 제공 인터페이스
 * 1일 100건 제한(1회로 DB 바로 저장 예정)
 * @author khj
 * @since 2025-12-10
 */

public interface DictionaryProvider {
    List<DictionaryProviderDto.Row> fetchTerms();
}
