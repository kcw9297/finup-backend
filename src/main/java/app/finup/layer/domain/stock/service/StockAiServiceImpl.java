package app.finup.layer.domain.stock.service;


import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.ChatProvider;
import app.finup.api.external.youtube.dto.YouTubeApiDto;
import app.finup.api.external.youtube.client.YouTubeClient;
import app.finup.layer.base.template.AiCodeTemplate;
import app.finup.layer.base.template.YouTubeCodeTemplate;
import app.finup.layer.domain.stock.constant.StockPrompt;
import app.finup.layer.domain.stock.constant.StockRedisKey;
import app.finup.layer.domain.stock.dto.StockAiDto;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.dto.StockDtoMapper;
import app.finup.layer.domain.stock.enums.ChartType;
import app.finup.layer.domain.stock.redis.StockRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * StockAiService 구현 클래스
 * @author kcw
 * @since 2026-01-05
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockAiServiceImpl implements StockAiService {

    // 사용 의존성
    private final ChatProvider chatProvider;
    private final StockRedisStorage stockRedisStorage;
    private final YouTubeClient youTubeClient;

    // 사용 상수
    private static final int MAX_DESCRIPTION_LENGTH = 150;
    private static final int MIN_RECOMMEND_LENGTH = 4;


    @Cacheable(
            value = StockRedisKey.CACHE_ANALYZE_CHART,
            key = "#stockCode + ':' + #chartType + ':' + #memberId"
    )
    @Override
    public StockAiDto.ChartAnalyzation analyzeChart(String stockCode, Long memberId, ChartType chartType) {
        return doAnalyzeChart(stockCode, memberId, chartType);
    }


    @CachePut(
            value = StockRedisKey.CACHE_ANALYZE_CHART,
            key = "#stockCode + ':' + #chartType + ':' + #memberId"
    )
    @Override
    public StockAiDto.ChartAnalyzation retryAnalyzeChart(String stockCode, Long memberId, ChartType chartType) {
        return doAnalyzeChart(stockCode, memberId, chartType);
    }


    // 차트 분석 메소드
    private StockAiDto.ChartAnalyzation doAnalyzeChart(String stockCode, Long memberId, ChartType chartType) {

        // [1] 현재 주식정보 조회
        StockDto.Info stockInfo = getStockInfo(stockCode);

        // [2] 프롬포트에 필요한 정보 조회 및 추출
        ChartAnalyzeRequest analyzeRequest = new ChartAnalyzeRequest(chartType, getCandles(chartType, stockInfo));
        StockAiDto.ChartAnalyzation prevAnalyze = stockRedisStorage.getPrevChartAnalyze(stockCode, memberId);

        // 프롬포트 파라미터
        Map<String, String> promptParams = new ConcurrentHashMap<>(Map.of(
                StockPrompt.INPUT, StrUtils.toJson(analyzeRequest),
                StockPrompt.PREV, StrUtils.toJson(prevAnalyze)
        ));

        // 프롬프트 생성
        String prompt = StrUtils.fillPlaceholder(StockPrompt.PROMPT_ANALYZE_CHART, promptParams);

        // [3] 주식 종목정보 기반 AI 분석 수행
        return AiCodeTemplate.analyzeWithPrev(
                chatProvider, prompt,
                result -> stockRedisStorage.storePrevChartAnalyze(stockCode, memberId, result)
        );
    }


    @Cacheable(
            value = StockRedisKey.CACHE_ANALYZE_DETAIL,
            key = "#stockCode + ':' + #memberId"
    )
    @Override
    public StockAiDto.DetailAnalyzation analyzeDetail(String stockCode, Long memberId) {
        return doAnalyzeDetail(stockCode, memberId);
    }


    @CachePut(
            value = StockRedisKey.CACHE_ANALYZE_DETAIL,
            key = "#stockCode + ':' + #memberId"
    )
    @Override
    public StockAiDto.DetailAnalyzation retryAnalyzeDetail(String stockCode, Long memberId) {
        return doAnalyzeDetail(stockCode, memberId);
    }


    // 주식 종목 상세 분석 메소드
    private StockAiDto.DetailAnalyzation doAnalyzeDetail(String stockCode, Long memberId) {

        // [1] 현재 주식정보 조회
        StockDto.Info stockInfo = getStockInfo(stockCode);

        // [2] 프롬포트에 필요한 정보 조회 및 추출
        StockDto.Detail detail = stockInfo.getDetail();
        StockAiDto.DetailAnalyzation prevAnalyze = stockRedisStorage.getPrevDetailAnalyze(stockCode, memberId);

        // 프롬포트 파라미터
        Map<String, String> promptParams = new ConcurrentHashMap<>(Map.of(
                StockPrompt.INPUT, StrUtils.toJson(detail),
                StockPrompt.PREV, StrUtils.toJson(prevAnalyze)
        ));

        // 프롬프트 생성
        String prompt = StrUtils.fillPlaceholder(StockPrompt.PROMPT_ANALYZE_DETAIL, promptParams);

        // [3] 주식 종목정보 기반 AI 분석 수행
        return AiCodeTemplate.analyzeWithPrev(
                chatProvider, prompt,
                result -> stockRedisStorage.storePrevDetailAnalyze(stockCode, memberId, result)
        );
    }


    // 차트 정보 조회
    private List<StockDto.Candle> getCandles(ChartType chartType, StockDto.Info stockInfo) {

        // [1] 주식 정보 내 차트 정보 조회
        StockDto.Chart chart = stockInfo.getChart();

        // [2] 차트 정보 내, 특정 기준 캔들 목록 반환
        return switch (chartType) {
            case DAY    -> chart.getDayCandles();
            case WEEK   -> chart.getWeekCandles();
            case MONTH  -> chart.getMonthCandles();
        };
    }


    // 내부에서 사용하는 임시 DTO
    private record ChartAnalyzeRequest(ChartType chartType, List<StockDto.Candle> candles) {}


    @Cacheable(
            value = StockRedisKey.CACHE_RECOMMEND_YOUTUBE,
            key = "#stockCode + ':' + #memberId"
    )
    @Override
    public List<StockAiDto.YouTubeRecommendation> recommendYouTube(String stockCode, Long memberId) {
        return doRecommendYouTube(stockCode, memberId);
    }


    @CachePut(
            value = StockRedisKey.CACHE_RECOMMEND_YOUTUBE,
            key = "#stockCode + ':' + #memberId"
    )
    @Override
    public List<StockAiDto.YouTubeRecommendation> retryRecommendYouTube(String stockCode, Long memberId) {
        return doRecommendYouTube(stockCode, memberId);
    }


    // 유튜브 영상 추천 메소드
    private List<StockAiDto.YouTubeRecommendation> doRecommendYouTube(String stockCode, Long memberId) {

        // [1] 주식 정보 조회
        StockDto.Info stockInfo = getStockInfo(stockCode);
        String stockName = stockInfo.getDetail().getStockName();

        // [2] 유튜브 검색 수행 후, 검색 결과 영상으로 다시 한번 상세 조회 수행
        List<YouTubeApiDto.Detail> searchResponses =
                YouTubeCodeTemplate.searchAndGetDetails(youTubeClient, stockName);

        // [3] AI 프롬포트 생성
        // Map<VideoId, YouTube.Detail>
        Map<String, YouTubeApiDto.Detail> candidates = searchResponses.stream()
                .collect(Collectors.toConcurrentMap(
                        YouTubeApiDto.Detail::getVideoId,
                        Function.identity()
                ));

        // 영상검색 결과에서, 일부만 추출하여 요청 생성 (모두 사용하면 지나친 토큰 사용)
        List<VideoRecommendRequest> input = candidates.values().stream().map(VideoRecommendRequest::from).toList();
        List<String> prev = stockRedisStorage.getPrevRecommendedVideoIds(stockCode, memberId);

        // 프롬포트 파라미터 생성
        Map<String, String> promptParams = new ConcurrentHashMap<>(Map.of(
                StockPrompt.INPUT, StrUtils.toJson(input),
                StockPrompt.PREV, StrUtils.toJson(prev),
                StockPrompt.STOCK_NAME, stockName,  // 추천 대상 종목명
                StockPrompt.RECOMMEND_AMOUNT, String.valueOf(MIN_RECOMMEND_LENGTH) // 추천 영상 개수
        ));

        // 프롬포트 생성
        String prompt = StrUtils.fillPlaceholder(StockPrompt.PROMPT_RECOMMEND_YOUTUBE, promptParams);

        // [4] 검색 결과 기반 AI 추천 수행
        return AiCodeTemplate.recommendWithPrev(
                        chatProvider, prompt, candidates, MIN_RECOMMEND_LENGTH,
                        prevIds -> stockRedisStorage.storePrevRecommendedVideoIds(stockCode, memberId, prevIds)
                )
                .stream()
                .map(StockDtoMapper::toYouTubeRecommend)
                .toList();
    }


    // Redis 내 주식 상세정보 조회
    private StockDto.Info getStockInfo(String stockCode) {

        // Redis 내 상세 종목 조회
        StockDto.Info stockInfo = stockRedisStorage.getStockInfo(stockCode);

        // 만약 정보가 없는 경우 예외 반환
        if (Objects.isNull(stockInfo)) throw new BusinessException(AppStatus.STOCK_NOT_FOUND);
        return stockInfo;
    }


    // 내부에서만 사용하는 임시 DTO
    // 추천 영상 요청을 위한 임시 클래스 (record)
    private record VideoRecommendRequest(
            String videoId,
            String title,
            String channelTitle,
            String description,
            Long viewCount,
            Long likeCount,
            Duration duration,
            Instant publishedAt
    ) {
        public static VideoRecommendRequest from(YouTubeApiDto.Detail detail) {
            return new VideoRecommendRequest(
                    detail.getVideoId(),
                    detail.getTitle(),
                    detail.getChannelTitle(),
                    StrUtils.splitWithStart(detail.getDescription(), MAX_DESCRIPTION_LENGTH),
                    detail.getViewCount(),
                    detail.getLikeCount(),
                    detail.getDuration(),
                    detail.getPublishedAt()
            );
        }
    }


}
