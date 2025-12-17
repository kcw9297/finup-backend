package app.finup.layer.domain.news.service;

import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.layer.domain.news.component.NewsContentExtractor;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.dto.NewsDtoMapper;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.service.WordsVectorService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsAiServiceImpl implements NewsAiService {

    private final AiManager aiManager;
    private final NewsContentExtractor extractor;
    private final WordsVectorService wordsVectorService;


    @Override
    public NewsDto.Ai analyzeDeep(String url) {
        String article = extractor.extract(url);
        if(article.isBlank()){
            return null;
        }
        //기사 임베딩
        String articleEmbedding = aiManager.embedJson(article);

        //관련 용어 단어벡터에서 검색
        List<WordsDto.Similarity> terms = wordsVectorService.similarity(articleEmbedding, 10);

        //용어 컨텍스트 생성
        String context = terms.stream()
                .filter(t -> t.getScore() < 0.65)
                .map(t -> "- " + t.getName() + " : " + t.getDescription())
                .reduce("", (a, b) -> a + b + "\n");

        String prompt = PromptTemplates.NEWS_ANALYSIS_DEEP
                .replace("{ARTICLE}", article)
                .replace("{TERMS}", context);

        Map<String, Object> result= aiManager.runJsonPrompt(prompt);
        //프롬프터가 뽑은 단어 검증
        //validateKeywords(result, terms);

        List<Map<String, String>> keywords = (List<Map<String, String>>) result.get("keywords");

        if(keywords != null) {
            List<Map<String, String>> mutable = new ArrayList<>(keywords);
            mutable.sort(Comparator.comparing(k -> k.get("term")));
            result.put("keywords", mutable);
        }

        return NewsDtoMapper.toAi(result);
    }

    @Override
    public NewsDto.Summary analyzeLight(String summary) {
        //기사 임베딩
        String articleEmbedding = aiManager.embedJson(summary);

        //관련 용어 단어벡터에서 검색
        List<WordsDto.Similarity> terms = wordsVectorService.similarity(articleEmbedding, 10);

        //용어 컨텍스트 생성
        String context = terms.stream()
                .filter(t -> t.getScore() < 0.65)
                .map(t -> "- " + t.getName() + " : " + t.getDescription())
                .reduce("", (a, b) -> a + b + "\n");

        String prompt = PromptTemplates.NEWS_SUMMARY_LIGHT
                .replace("{ARTICLE}", summary)
                .replace("{TERMS}", context);

        Map<String, Object> result= aiManager.runJsonPrompt(prompt);
        //프롬프터가 뽑은 단어 검증
        //validateKeywords(result, terms);

        List<Map<String, String>> keywords = (List<Map<String, String>>) result.get("keywords");

        if(keywords != null){
            List<Map<String, String>> mutable = new ArrayList<>(keywords);
            mutable.sort(Comparator.comparing(k -> k.get("term")));
            result.put("keywords", mutable);
        }

        return NewsDtoMapper.toSummary(result);
    }

    private void validateKeywords(
            Map<String, Object> result,
            List<WordsDto.Similarity> terms
    ) {
        Set<String> allowedTerms = terms.stream()
                .map(WordsDto.Similarity::getName)
                .collect(Collectors.toSet());

        List<Map<String, String>> keywords =
                (List<Map<String, String>>) result.get("keywords");

        if (keywords == null) return;

        List<Map<String, String>> filtered = keywords.stream()
                .filter(k -> allowedTerms.contains(k.get("term")))
                .collect(Collectors.toList());

        if (filtered.size() != keywords.size()) {
            log.warn("[AI] 후보에 없는 키워드 제거됨: before={}, after={}",
                    keywords.size(), filtered.size());
        }

        result.put("keywords", filtered);
    }



}
