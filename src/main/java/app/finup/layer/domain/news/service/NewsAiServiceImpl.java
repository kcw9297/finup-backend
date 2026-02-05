package app.finup.layer.domain.news.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.ChatProvider;
import app.finup.layer.base.template.AiCodeTemplate;
import app.finup.layer.domain.news.constant.NewsPrompt;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import app.finup.layer.domain.news.dto.NewsAiDto;
import app.finup.layer.domain.news.entity.News;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NewsAiService 구현 클래스
 * @author kcw
 * @since 2025-12-25
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsAiServiceImpl implements NewsAiService {

    // 사용 의존성
    private final NewsRepository newsRepository;
    private final NewsRedisStorage newsRedisStorage;
    private final ChatProvider chatProvider;

    // 사용 상수
    private static final int MAX_LENGTH_DESCRIPTION = 6000;
    private static final int AMOUNT_ANALYSIS_WORDS = 5;


    @Cacheable(
            value = NewsRedisKey.CACHE_ANALYZE,
            key = "#newsId + ':' + #memberId"
    )
    @Override
    public String getAnalysis(Long newsId, Long memberId) {
        return processAnalysis(newsId, memberId);
    }


    @CachePut(
            value = NewsRedisKey.CACHE_ANALYZE,
            key = "#newsId + ':' + #memberId"
    )
    @Override
    public String retryAndGetAnalyze(Long newsId, Long memberId) {
        return processAnalysis(newsId, memberId);
    }


    // 뉴스 분석 수행
    private String processAnalysis(Long newsId, Long memberId) {

        // [1] 기존 뉴스 정보 조회
        News news = newsRepository
                .findById(newsId)
                .orElseThrow(() -> new BusinessException(AppStatus.NEWS_NOT_FOUND));

        // [2] AI 프롬포트에 필요한 파라미터 생성
        String description = StrUtils.removeEmptySpace(// 과도한 공백 제거 (줄바꿈, 과도한 띄어쓰기 등..)
                StrUtils.splitWithStart(news.getDescription(), MAX_LENGTH_DESCRIPTION) // 최대 길이 제한
        );

        // 이전 분석정보 및, 현재 분석요청 객체 생성
        NewsAnalyzeRequest input = new NewsAnalyzeRequest(news.getTitle(), news.getSummary(), description);
        String prev = newsRedisStorage.getPrevAnalyze(newsId, memberId);

        // 프롬포트 파라미터
        Map<String, String> promptParams = new HashMap<>();
        promptParams.put(NewsPrompt.INPUT, StrUtils.toJson(input));
        promptParams.put(NewsPrompt.PREV, prev);

        // 프롬프트 생성
        String prompt = StrUtils.fillPlaceholder(NewsPrompt.PROMPT_ANALYZE, promptParams);

        // [3] AI분석 수행
        return AiCodeTemplate.sendQueryAndGetStringWithPrev(
                chatProvider, prompt,
                result -> newsRedisStorage.storePrevAnalyze(newsId, memberId, result)
        );
    }


    // 뉴스 분석 요청 임시 DTO
    private record NewsAnalyzeRequest(String title, String summary, String description) {}


    @Cacheable(
            value = NewsRedisKey.CACHE_ANALYZE_WORDS,
            key = "#newsId + ':' + #memberId"
    )
    @Override
    public List<NewsAiDto.AnalysisWords> getAnalysisWords(Long newsId, Long memberId) {
        return processAnalysisWords(newsId, memberId);
    }


    @CachePut(
            value = NewsRedisKey.CACHE_ANALYZE_WORDS,
            key = "#newsId + ':' + #memberId"
    )
    @Override
    public List<NewsAiDto.AnalysisWords> retryAndGetAnalysisWords(Long newsId, Long memberId) {
        return processAnalysisWords(newsId, memberId);
    }


    // 단어 분석 수행
    private List<NewsAiDto.AnalysisWords> processAnalysisWords(Long newsId, Long memberId) {

        // [1] 뉴스 조회
        News news = newsRepository
                .findById(newsId)
                .orElseThrow(() -> new BusinessException(AppStatus.NEWS_NOT_FOUND));

        // [2] 뉴스 제목/본문 추출
        String title = news.getTitle();
        String description = StrUtils.splitWithStart(news.getDescription(), MAX_LENGTH_DESCRIPTION);

        // [3] 이전 단어 목록 조회
        List<String> prevAnalysisWords = newsRedisStorage.getPrevAnalysisWords(newsId, memberId);

        // [4] 프롬프트 생성
        // 프롬포트 파라미터
        Map<String, String> params = Map.of(
                NewsPrompt.INPUT, StrUtils.toJson(new NewsWordsAnalysisRequest(title, description)),
                NewsPrompt.ANALYSIS_WORDS_AMOUNT, String.valueOf(AMOUNT_ANALYSIS_WORDS),
                NewsPrompt.PREV_ANALYSIS_WORDS, StrUtils.toJson(prevAnalysisWords)
        );

        // 프롬포트 생성
        String prompt = StrUtils.fillPlaceholder(NewsPrompt.PROMPT_ANALYSIS_NEWS_WORDS, params);

        // [5] 추천 수행
        return AiCodeTemplate.recommendWithPrevAndNoCandidates(
                chatProvider, prompt, NewsAiDto.AnalysisWords.class,
                result -> newsRedisStorage.storePrevAnalysisWords(newsId, memberId, result)
        );
    }


    // 사용 임시 DTO
    private record NewsWordsAnalysisRequest(String name, String description) {}
}
