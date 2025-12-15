package app.finup.layer.domain.news.component;

import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.repository.WordsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;

/**
 * 단어 벡터 초기화 클래스
 * @author oyh
 * @since 2025-12-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordVectorInitializer {
    private final VectorStore vectorStore;
    private final WordsRepository wordsRepository;

    @PostConstruct
    public void init(){
        var existCheck = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("채권면역전략")
                        .topK(1)
                        .build()
        );

        if (!existCheck.isEmpty()) {
            log.info("✅ Chroma already initialized. Skip word loading.");
            logVectorResult("채권면역전략", existCheck);
            return;
        }
        Words sample = wordsRepository.findByName("채권면역전략")
                .orElseThrow(() ->
                        new IllegalStateException("❌ 테스트용 단어가 DB에 없습니다: 채권면역전략"));
        Document document = toDocument(sample);

        log.info("🔄 Loading sample word into ChromaDB...");
        vectorStore.add(List.of(document));
        log.info("✅ Sample word loaded into ChromaDB");

        var result = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("채권면역전략")
                        .topK(3)
                        .build()
        );

        logVectorResult("채권면역전략", result);

//        List<Words> words = wordsRepository.findAll();
//        log.info("🔄 Loading {} words into ChromaDB...", words.size());
//        List<Document> documents = words.stream()
//                .map(this::toDocument)
//                .toList();
//        vectorStore.add(documents);
//        log.info("✅ Loaded {} words into ChromaDB", documents.size());
    }

    private void logVectorResult(String query, List<Document> results) {
        log.info("===== VECTOR SEARCH RESULT (query={}) =====", query);
        if (results.isEmpty()) {
            log.info("❌ No result");
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            Document d = results.get(i);
            log.info(
                    "{}. termId={} name={}",
                    i + 1,
                    d.getId(),
                    d.getMetadata().get("name")
            );
        }
        log.info("=========================================");
    }

    private Document toDocument(Words words) {
        return new Document(
                String.valueOf(words.getTermId()),   // document id
                buildEmbeddingText(words),           // embedding 대상 텍스트
                Map.of(
                        "termId", words.getTermId(),
                        "name", words.getName()
                )
        );
    }

    //용어: {name}\n설명: {description(또는 short)}
    private String buildEmbeddingText(Words words) {
        return """
        용어: %s
        설명: %s
        """.formatted(
                words.getName(),
                normalize(words.getDescription())
        );
    }

    private String normalize(String text) {
        if (text == null) return "";
        return text
                .replaceAll("\\s+", " ")
                .trim();
    }
}
