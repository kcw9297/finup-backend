package app.finup.layer.domain.news.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/words/vector")
public class WordsVectorSearchController {
    private final WordsVectorSearchService wordsVectorSearchService;
    private final VectorStore vectorStore;

    @PostMapping("/search")
    public List<WordSearchResponse> search(@RequestBody WordSearchRequest req){
        int topK = (req.getTopK() == null || req.getTopK() <= 0) ? 10 : req.getTopK();

        log.info("🔍 Vector search 요청");

        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(req.getText())
                        .topK(topK)
                        .similarityThreshold(0.0)
                        .build());

        log.info("🔍 검색 결과 개수 = {}", docs.size());

        docs.forEach(d -> {
            log.info(
                    "▶ score={}, termId={}, text={}",
                    d.getScore(),
                    d.getMetadata().get("termId"),
                    d.getText()
            );
        });

        return docs.stream()
                .map(WordSearchResponse::from)
                .toList();

    }
}
