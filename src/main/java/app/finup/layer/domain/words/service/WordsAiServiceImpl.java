package app.finup.layer.domain.words.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.AiUtils;
import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.ChatProvider;
import app.finup.infra.ai.EmbeddingProvider;
import app.finup.layer.base.template.AiCodeTemplate;
import app.finup.layer.domain.news.entity.News;
import app.finup.layer.domain.news.repository.NewsRepository;
import app.finup.layer.domain.quiz.constant.QuizPrompt;
import app.finup.layer.domain.words.constant.WordsPrompt;
import app.finup.layer.domain.words.constant.WordsRedisKey;
import app.finup.layer.domain.words.dto.WordsAiDto;
import app.finup.layer.domain.words.dto.WordsDtoMapper;
import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.redis.WordsRedisStorage;
import app.finup.layer.domain.words.repository.WordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WordsAiService 구현체
 * @author kcw
 * @since 2026-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WordsAiServiceImpl implements WordsAiService {

    // 사용 의존성
    private final WordsRepository wordsRepository;
    private final NewsRepository newsRepository;
    private final WordsRedisStorage wordsRedisStorage;
    private final ChatProvider chatProvider;
    private final EmbeddingProvider embeddingProvider;

    // 사용 상수
    private static final int LENGTH_SPLIT_DESCRIPTION = 800;
    private static final int AMOUNT_FIND = 50;
    private static final int AMOUNT_RECOMMENDATION = 10;


    @Cacheable(
            value = WordsRedisKey.CACHE_RECOMMENDATION_NEWS,
            key = "#newsId + ':' + #memberId"
    )
    @Override
    public List<WordsAiDto.Recommendation> getRecommendationNewsWords(Long newsId, Long memberId) {
        return doRecommendations(newsId, memberId);
    }


    @CachePut(
            value = WordsRedisKey.CACHE_RECOMMENDATION_NEWS,
            key = "#newsId + ':' + #memberId"
    )
    @Override
    public List<WordsAiDto.Recommendation> retryAndGetRecommendationNewsWords(Long newsId, Long memberId) {
        return doRecommendations(newsId, memberId);
    }


    private List<WordsAiDto.Recommendation> doRecommendations(Long newsId, Long memberId) {

        // [1] 뉴스 조회
        News news = newsRepository
                .findById(newsId)
                .orElseThrow(() -> new BusinessException(AppStatus.NEWS_NOT_FOUND));

        // [2] 뉴스 제목/본문 벡터화
        String title = news.getTitle();
        String description = StrUtils.splitWithStart(news.getDescription(), LENGTH_SPLIT_DESCRIPTION);
        String embeddingText = AiUtils.generateEmbeddingText(title, description);
        byte[] embedding = embeddingProvider.generate(embeddingText);

        // [3] 임베딩 텍스트로 단어 조회 (코사인 유사도 검색)
        List<Long> prevRecommendationIds = wordsRedisStorage.getPrevRecommendationIds(newsId, memberId);
        if (prevRecommendationIds.isEmpty()) prevRecommendationIds = List.of(-1L); // JPA는 빈 콜렉션 처리 불가
        Map<Long, WordsAiDto.Recommendation> candidates =  // Map<ID, Words>
                wordsRepository
                        .findWithSimilarExcludePrev(embedding, prevRecommendationIds, AMOUNT_FIND)
                        .stream()
                        .collect(Collectors.toMap(
                                Words::getTermId,
                                WordsDtoMapper::toRecommendation
                        ));

        // [4] 프롬프트 생성
        // 프롬포트 파라미터
        Map<String, String> params = Map.of(
                WordsPrompt.INPUT_NEWS, StrUtils.toJson(new RecommendationNews(title, description)),
                WordsPrompt.INPUT_WORDS, StrUtils.toJson(candidates.values()),
                WordsPrompt.RECOMMENDATION_AMOUNT, String.valueOf(AMOUNT_RECOMMENDATION),
                WordsPrompt.PREV_RECOMMENDATION_IDS, StrUtils.toJson(prevRecommendationIds)
        );

        // 프롬포트
        String prompt = StrUtils.fillPlaceholder(WordsPrompt.PROMPT_RECOMMENDATION_NEWS_WORDS, params);

        // [5] 추천 수행
        return AiCodeTemplate.recommendWithPrev(
                chatProvider, prompt, candidates, Long.class, AMOUNT_RECOMMENDATION,
                result -> wordsRedisStorage.storePrevRecommendationIds(newsId, memberId, result)
        );
    }


    // 사용 임시 DTO
    private record RecommendationNews(String title, String description) {}
}
