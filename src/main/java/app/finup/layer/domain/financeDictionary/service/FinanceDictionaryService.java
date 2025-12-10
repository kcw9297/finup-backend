package app.finup.layer.domain.financeDictionary.service;

public interface FinanceDictionaryService {
    void refreshTerms();  // 외부 Provider -> 내부 DB 갱신
}
