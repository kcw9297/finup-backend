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
public class WordVectorTestService {

    private final VectorStore vectorStore;

    public void testByArticle(String article) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(article)
                        .topK(20)
                        .build()
        );

        log.info("===== VECTOR SEARCH RESULT =====");
        for (int i = 0; i < results.size(); i++) {
            Document d = results.get(i);
            log.info(
                    "{}. termId={} name={}",
                    i + 1,
                    d.getId(),
                    d.getMetadata().get("name")
            );
        }
        log.info("================================");
    }
}
