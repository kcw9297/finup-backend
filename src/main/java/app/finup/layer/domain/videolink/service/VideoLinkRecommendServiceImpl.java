package app.finup.layer.domain.videolink.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.LogUtils;
import app.finup.infra.ai.provider.EmbeddingProvider;
import app.finup.infra.ai.utils.AiUtils;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.study.repository.StudyRepository;
import app.finup.layer.domain.videolink.constant.VideoLinkCache;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.videolink.dto.VideoLinkDtoMapper;
import app.finup.layer.domain.videolink.manager.VideoLinkAiManager;
import app.finup.layer.domain.videolink.redis.VideoLinkRedisStorage;
import app.finup.layer.domain.videolink.repository.VideoLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * VideoLinkService 구현 클래스
 * @author kcw
 * @since 2025-12-04
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VideoLinkRecommendServiceImpl implements VideoLinkRecommendService {

    // 사용 의존성
    private final VideoLinkRepository videoLinkRepository;
    private final VideoLinkRedisStorage videoLinkRedisStorage;
    private final VideoLinkAiManager videoLinkAiManager;
    private final EmbeddingProvider embeddingProvider;
    private final StudyRepository studyRepository;

    // 사용 상수
    private static final int RECOMMEND_AMOUNT_REQUEST = 20; // DB에 요청하는 추천 영상 개수
    private static final int RECOMMEND_AMOUNT_RESPONSE = 6; // 최대 추천 영상 수
    private static final int RECOMMEND_AMOUNT_LOGOUT = 6; // 페이지 홈의 로그아웃 회원에게 제공하는 영상 수
    private static final double RECOMMEND_THRESHOLD = 0.75; // 페이지 홈의 로그아웃 회원에게 제공하는 영상 수


    @Cacheable( // 홈은 단일 캐시 (모두에게 공용으로 보임)
            value = VideoLinkCache.RECOMMEND_HOME_LOGOUT,
            key = "'DEFAULT'"
    )
    @Override
    @Transactional(readOnly = true)
    public List<VideoLinkDto.Row> recommendForLogoutHome() {

        // [1] AI에게 키워드 추천 (공백 기준으로 나누어진 키워드 목록)
        String sentence = videoLinkAiManager.recommendSentenceForLogoutHome();
        LogUtils.showWarn(this.getClass(), "AI SENTENCE = %s", sentence);

        // [2] 추천받은 키워드 기반 embedded 배열 생성
        byte[] embedding = embeddingProvider.generate(sentence);

        // [3] embedded 기반 영상 추천 후, 결과 반환
        return videoLinkRepository
                .findSimilarWithThreshold(embedding, RECOMMEND_AMOUNT_LOGOUT, RECOMMEND_THRESHOLD)
                .stream()
                .map(VideoLinkDtoMapper::toRow)
                .toList();
    }


    @Cacheable( // 캐싱 데이터 사용
            value = VideoLinkCache.RECOMMEND_HOME_LOGIN,
            key = "#memberId"
    )
    @Override
    public List<VideoLinkDto.Row> recommendForLoginHome(Long memberId) {
        return doRecommendForHome(memberId, false);
    }


    @CachePut( // 기존 Cache 덮어 쓰기
            value = VideoLinkCache.RECOMMEND_HOME_LOGIN,
            key = "#memberId"
    )
    public List<VideoLinkDto.Row> retryRecommendForLoginHome(Long memberId) {
        return doRecommendForHome(memberId, true);
    }


    // 페이지 홈에 추천할 영상 조회
    private List<VideoLinkDto.Row> doRecommendForHome(Long memberId, boolean retry) {

        // [1] 과거 키워드 조회
        String latestSentences = videoLinkRedisStorage.getLatestSentenceForHome(memberId);

        // [2] 키워드 추천
        String sentence = videoLinkAiManager.recommendSentenceForLoginHome(latestSentences);
        LogUtils.showWarn(this.getClass(), "AI SENTENCE = %s", sentence);

        // [3] 추천된 키워드 기반 임베딩 배열 생성 & 이전 추천영상번호 조회
        byte[] embedding = embeddingProvider.generate(sentence);
        List<Long> recommendedIds = videoLinkRedisStorage.getLatestRecommendedIds(memberId);

        // [4] 임베딩 기반 영상 추천
        List<VideoLinkDto.Row> candidates = videoLinkRepository
                .findSimilarWithThreshold(embedding, RECOMMEND_AMOUNT_REQUEST, RECOMMEND_THRESHOLD)
                .stream()
                .filter(videoLink -> !recommendedIds.contains(videoLink.getVideoLinkId()))
                .map(VideoLinkDtoMapper::toRow)
                .collect(Collectors.toList()); // 가변 배열로 저장

        // 재시도인 경우 목록 셔플
        if (retry) Collections.shuffle(candidates);
        List<VideoLinkDto.Row> results = candidates.stream().limit(RECOMMEND_AMOUNT_RESPONSE).toList();

        // [5] 현재 추천 결과 정보들을 저장
        if (!results.isEmpty()) {
            videoLinkRedisStorage.storeLatestSentenceForHome(sentence, memberId);
            videoLinkRedisStorage.storeLatestRecommendedIds(
                    results.stream().map(row -> String.valueOf(row.getVideoLinkId())).toList(), memberId
            );
        }
        return candidates;
    }


    @Cacheable( // 캐싱 데이터 사용
            value = VideoLinkCache.RECOMMEND_STUDY,
            key = "#studyId + ':' + #memberId"
    )
    @Override
    @Transactional(readOnly = true)
    public List<VideoLinkDto.Row> recommendForStudy(Long studyId, Long memberId) {
        return doRecommendForStudy(studyId, memberId, false);
    }


    @CachePut( // 기존 Cache 덮어 쓰기
            value = VideoLinkCache.RECOMMEND_STUDY,
            key = "#studyId + ':' + #memberId"
    )
    @Override
    public List<VideoLinkDto.Row> retryRecommendForStudy(Long studyId, Long memberId) {
        return doRecommendForStudy(studyId, memberId, true);
    }


    // 페이지 홈에 추천할 영상 조회
    private List<VideoLinkDto.Row> doRecommendForStudy(Long studyId, Long memberId, boolean retry) {

        // [1] 현재 대상 학습정보 조회
        Study study = studyRepository
                .findById(studyId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));

        // [2] 이전 추천영상번호 조회 및 데이터 가공
        List<Long> recommendedIds = videoLinkRedisStorage.getLatestRecommendedIds(memberId);
        String levelText = switch(study.getLevel()) {
            case 1 -> "입문 초급";
            case 2 -> "초중급";
            case 3 -> "중급";
            case 4 -> "중고급";
            case 5 -> "고급 실전";
            default -> "";
        };

        // [3] 임베딩 요청 문자열 생성 및 임베딩 수행
        String text = AiUtils.generateEmbeddingText(study.getName(), study.getSummary(), study.getDetail(), levelText);
        byte[] embedding = embeddingProvider.generate(text);

        // [4] 임베딩 기반 영상 추천 후 반환
        List<VideoLinkDto.Row> candidates = videoLinkRepository
                .findSimilarWithThreshold(embedding, RECOMMEND_AMOUNT_REQUEST, RECOMMEND_THRESHOLD)
                .stream()
                .filter(videoLink -> !recommendedIds.contains(videoLink.getVideoLinkId()))
                .map(VideoLinkDtoMapper::toRow)
                .collect(Collectors.toList()); // 가변 배열로 저장

        // 재시도인 경우 목록 셔플
        if (retry) Collections.shuffle(candidates);
        List<VideoLinkDto.Row> results = candidates.stream().limit(RECOMMEND_AMOUNT_RESPONSE).toList();

        // [5] 현재 추천 검색 문장 저장
        if (!results.isEmpty()) {
            videoLinkRedisStorage.storeLatestRecommendedIds(
                    results.stream().map(row -> String.valueOf(row.getVideoLinkId())).toList(), memberId
            );
        }
        return results;
    }



}
