package app.finup.layer.domain.news.redis;

import app.finup.common.utils.StrUtils;
import app.finup.layer.base.template.RedisCodeTemplate;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import app.finup.layer.domain.news.dto.NewsAiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * NewsRedisStorage 구현 클래스
 * @author kcw
 * @since 2026-01-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsRedisStorageImpl implements NewsRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final Duration TTL_ANALYZE = Duration.ofDays(1);
    private static final Duration TTL_ANALYZE_WORDS = Duration.ofDays(1);
    private static final int AMOUNT_PREV_ANALYSIS_WORDS_MAX = 10;


    @Override
    public void storePrevAnalyze(Long newsId, Long memberId, String analyzation) {
        srt.opsForValue().set(getKey(NewsRedisKey.KEY_ANALYZE, newsId, memberId), analyzation, TTL_ANALYZE);
    }


    @Override
    public String getPrevAnalyze(Long newsId, Long memberId) {
        return srt.opsForValue().get(getKey(NewsRedisKey.KEY_ANALYZE, newsId, memberId));
    }

    @Override
    public void storePrevAnalysisWords(Long newsId, Long memberId, List<NewsAiDto.AnalysisWords> wordNames) {

        RedisCodeTemplate.addPrevList(
                srt,
                getKey(NewsRedisKey.KEY_ANALYZE_WORDS, newsId, memberId),
                wordNames.stream().map(NewsAiDto.AnalysisWords::getName).toList(), // 단어명만 추출해서 저장
                AMOUNT_PREV_ANALYSIS_WORDS_MAX,
                TTL_ANALYZE_WORDS
        );
    }

    @Override
    public List<String> getPrevAnalysisWords(Long newsId, Long memberId) {
        return RedisCodeTemplate.getPrevList(srt, getKey(NewsRedisKey.KEY_ANALYZE_WORDS, newsId, memberId));
    }


    // key 조립 수행
    private String getKey(String baseKey, Long newsId, Long memberId) {

        return StrUtils.fillPlaceholder(
                baseKey,
                Map.of(NewsRedisKey.NEWS_ID, String.valueOf(newsId), NewsRedisKey.MEMBER_ID, String.valueOf(memberId))
        );
    }
}
