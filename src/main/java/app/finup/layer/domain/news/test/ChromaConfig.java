package app.finup.layer.domain.news.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ChromaConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestFactory(new SimpleClientHttpRequestFactory());
    }

    @Bean
    public ChromaApi chromaApi(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        return new ChromaApi(
                "http://localhost:8000",
                restClientBuilder,
                objectMapper
        );
    }

    @Bean
    public VectorStore vectorStore(
            EmbeddingModel embeddingModel,
            ChromaApi chromaApi
    ) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .tenantName("default_tenant")
                .databaseName("default_database")
                .collectionName("finup-news")
                .initializeSchema(false)
                .build();
    }
}
