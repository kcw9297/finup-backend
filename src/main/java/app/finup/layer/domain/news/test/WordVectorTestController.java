package app.finup.layer.domain.news.test;

import app.finup.common.constant.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(Url.NEWS_PUBLIC)
@RequiredArgsConstructor
public class WordVectorTestController {
    private final WordVectorTestService testService;

    @PostMapping("/vector-search")
    public void test(@RequestBody VectorSearchRequest vsr){
        testService.testByArticle(vsr.getArticle());
    }
}
