package app.finup.layer.domain.news.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordSearchRequest {
    private String text;   // 기사 본문 or 문장
    private Integer topK;
}
