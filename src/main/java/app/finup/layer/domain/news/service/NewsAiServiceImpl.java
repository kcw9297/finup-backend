package app.finup.layer.domain.news.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.ChatProvider;
import app.finup.layer.base.template.AiCodeTemplate;
import app.finup.layer.domain.news.constant.NewsPrompt;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import app.finup.layer.domain.news.entity.News;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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


    @Cacheable(
            value = NewsRedisKey.CACHE_ANALYZE,
            key = "#newsId + ':' + #memberId"
    )
    @Override
    public String analyze(Long newsId, Long memberId) {
        return doAnalyze(newsId, memberId);
    }


    @CachePut(
            value = NewsRedisKey.CACHE_ANALYZE,
            key = "#newsId + ':' + #memberId"
    )
    @Override
    public String retryAnalyze(Long newsId, Long memberId) {
        return doAnalyze(newsId, memberId);
    }


    private String doAnalyze(Long newsId, Long memberId) {
        // [1] 기존 뉴스 정보 조회
        News news = newsRepository
                .findById(newsId)
                .orElseThrow(() -> new BusinessException(AppStatus.NEWS_NOT_FOUND));

        // [2] AI 프롬포트에 필요한 파라미터 생성
        String description = StrUtils.splitWithStart(news.getDescription(), MAX_LENGTH_DESCRIPTION); // 최대 길이 제한
        NewsAnalyzeRequest input = new NewsAnalyzeRequest(news.getTitle(), news.getSummary(), description);
        String prev = newsRedisStorage.getPrevAnalyze(newsId, memberId);

        // 프롬포트 파라미터
        Map<String, String> promptParams = new ConcurrentHashMap<>(Map.of(
                NewsPrompt.INPUT, StrUtils.toJson(input),
                NewsPrompt.PREV, prev
        ));

        // 프롬프트 생성
        String prompt = StrUtils.fillPlaceholder(NewsPrompt.PROMPT_ANALYZE, promptParams);

        // [3] AI분석 수행
        return AiCodeTemplate.sendQueryAndGetJsonWithPrev(
                chatProvider, prompt,
                result -> newsRedisStorage.storePrevAnalyze(newsId, memberId, result)
        );
    }


    // 뉴스 분석 요청 임시 DTO
    private record NewsAnalyzeRequest(String title, String summary, String description) {}
}
