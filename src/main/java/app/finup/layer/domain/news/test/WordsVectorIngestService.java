package app.finup.layer.domain.news.test;

import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.repository.WordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordsVectorIngestService {
    private final WordsRepository wordsRepository;
    private final VectorStore vectorStore;

    public void ingestAllWords(){
        List<Words> wordsList = wordsRepository.findAll();
        List<Document> docs = wordsList.stream()
                .map(w -> new Document(
                        "용어: " + w.getName() +
                                "\n설명: " + w.getDescription(),
                        Map.of(
                                "termId", w.getTermId(),
                                "type", "WORD",
                                "docId", "word-" + w.getTermId()
                        )
                ))
                .toList();

        vectorStore.add(docs);

        log.info("📦 VectorStore.add 완료 ({}건)", docs.size());
    }

//    private Document toDocument(Words word) {
//        return new Document(
//                buildContent(word),
//                Map.of(
//                        "termId", word.getTermId(),
//                        "name", word.getName(),
//                        "type", "word"
//                )
//        );
//    }
//
//    private String buildContent(Words word) {
//        return """
//                용어: %s
//                설명: %s
//                """.formatted(
//                word.getName(),
//                word.getDescription()
//        );
//    }

}
