package app.finup.layer.domain.news.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChromaCollectionInitializer {

    private final ChromaApi chromaApi;
    private static final String TENANT = "default";
    private static final String DATABASE = "default";
    private static final String COLLECTION = "words";

    @PostConstruct
    public void init() {
        try {
            chromaApi.getCollection(TENANT, DATABASE, COLLECTION);
            log.info("✅ Chroma collection [{}] already exists", COLLECTION);
        } catch (Exception e) {
            log.info("🔧 Creating Chroma collection [{}]", COLLECTION);
            ChromaApi.CreateCollectionRequest request =
                    new ChromaApi.CreateCollectionRequest(
                            COLLECTION,
                            null
                    );
            chromaApi.createCollection(TENANT, DATABASE, request);
            log.info("✅ Chroma collection [{}] created", COLLECTION);
        }
    }
}
