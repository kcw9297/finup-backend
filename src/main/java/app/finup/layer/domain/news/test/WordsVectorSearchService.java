package app.finup.layer.domain.news.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordsVectorSearchService {
    private final VectorStore vectorStore;

    public List<WordSearchResponse> findRelatedWords(String text, int topK){
        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder().query(text).topK(topK).build());
        log.info("🔍 검색 결과 개수 = {}", results.size());

        return results.stream()
                .map(doc -> WordSearchResponse.from(doc))
                .toList();
    }
}
