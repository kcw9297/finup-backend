package app.finup.layer.domain.news.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.document.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordSearchResponse {
    private Long termId;
    private String content;
    private double score;

    public static WordSearchResponse from(Document doc) {
        return new WordSearchResponse(
                ((Number) doc.getMetadata().get("termId")).longValue(),
                doc.getText(),
                doc.getScore()
        );
    }
}
