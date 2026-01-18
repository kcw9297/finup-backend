package app.finup.layer.domain.videolink.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.ChatProvider;
import app.finup.infra.ai.EmbeddingProvider;
import app.finup.layer.base.template.AiCodeTemplate;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.study.repository.StudyRepository;
import app.finup.layer.domain.videolink.constant.VideoLinkPrompt;
import app.finup.layer.domain.videolink.constant.VideoLinkRedisKey;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.videolink.dto.VideoLinkDtoMapper;
import app.finup.layer.domain.videolink.redis.VideoLinkRedisStorage;
import app.finup.layer.domain.videolink.repository.VideoLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * VideoLinkService 구현 클래스
 * @author kcw
 * @since 2025-12-04
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoLinkAiServiceImpl implements VideoLinkAiService {

    // 사용 의존성
    private final VideoLinkRepository videoLinkRepository;
    private final VideoLinkRedisStorage videoLinkRedisStorage;
    private final EmbeddingProvider embeddingProvider;
    private final StudyRepository studyRepository;
    private final ChatProvider chatProvider;

    // 사용 상수
    private static final int RECOMMEND_AMOUNT_REQUEST = 20; // DB에 요청하는 추천 영상 개수
    private static final int RECOMMEND_AMOUNT_RESPONSE = 6; // 최대 추천 영상 수
    private static final int RECOMMEND_AMOUNT_LOGOUT = 6; // 페이지 홈의 로그아웃 회원에게 제공하는 영상 수
    private static final double RECOMMEND_THRESHOLD = 0.85; // 영상 유사도 기준 (낮을수록 연관성 높음)


    @Cacheable( // 홈은 단일 캐시 (모두에게 공용으로 보임)
            value = VideoLinkRedisKey.CACHE_RECOMMEND_HOME_LOGOUT,
            key = "'DEFAULT'"
    )
    @Override
    @Transactional(readOnly = true)
    public List<VideoLinkDto.Row> recommendForLogoutHome() {

        // [1] AI에게 키워드 추천 (공백 기준으로 나누어진 키워드 목록)
        String prompt = StrUtils.fillPlaceholder( // 프롬포트 생성
                VideoLinkPrompt.PROMPT_RECOMMEND_SENTENCE_HOME,
                Map.of(VideoLinkPrompt.LATEST_SENTENCES, "") // 홈은 과거이력 없음
        );

        // [2] 추천 문자열 생성
        String sentence = AiCodeTemplate.sendQueryAndGetString(chatProvider, prompt);
        LogUtils.showWarn(this.getClass(), "AI SENTENCE = %s", sentence);

        // [2] 추천받은 키워드 기반 embedded 배열 생성
        byte[] embedding = embeddingProvider.generate(sentence);

        // [3] embedded 기반 영상 추천 후, 결과 반환
        return videoLinkRepository
                .findSimilar(embedding, RECOMMEND_AMOUNT_REQUEST)
                .stream()
                .map(VideoLinkDtoMapper::toRow)
                .collect(Collectors.toList()); // 가변 배열로 저장
    }


    @Cacheable( // 캐싱 데이터 사용
            value = VideoLinkRedisKey.CACHE_RECOMMEND_HOME_LOGIN,
            key = "#memberId"
    )
    @Override
    public List<VideoLinkDto.Row> recommendForLoginHome(Long memberId) {
        return doRecommendForHome(memberId, false);
    }


    @CachePut( // 기존 Cache 덮어 쓰기
            value = VideoLinkRedisKey.CACHE_RECOMMEND_HOME_LOGIN,
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
        String prompt = StrUtils.fillPlaceholder( // 프롬포트 생성
                VideoLinkPrompt.PROMPT_RECOMMEND_SENTENCE_HOME,
                Map.of(VideoLinkPrompt.LATEST_SENTENCES, latestSentences)
        );

        // 추천 문자열 생성
        String sentence = AiCodeTemplate.sendQueryAndGetString(chatProvider, prompt);
        LogUtils.showWarn(this.getClass(), "AI SENTENCE = %s", sentence);

        // [3] 추천된 키워드 기반 임베딩 배열 생성 & 이전 추천영상번호 조회
        byte[] embedding = embeddingProvider.generate(sentence);
        List<Long> recommendedIds = videoLinkRedisStorage.getLatestRecommendedIds(memberId);

        // [4] 임베딩 기반 영상 추천
        List<VideoLinkDto.Row> candidates = videoLinkRepository
                .findSimilarWithExcluding(embedding, RECOMMEND_AMOUNT_REQUEST, recommendedIds)
                .stream()
                .map(VideoLinkDtoMapper::toRow)
                .collect(Collectors.toList()); // 가변 배열로 저장

        // 재시도인 경우 목록 셔플
        if (retry) Collections.shuffle(candidates);

        // [5] 현재 추천 결과 정보들을 저장
        if (!candidates.isEmpty()) {
            videoLinkRedisStorage.storeLatestSentenceForHome(sentence, memberId);
            videoLinkRedisStorage.storeLatestRecommendedIds(
                    candidates.stream().map(row -> String.valueOf(row.getVideoLinkId())).toList(), memberId
            );
        }
        return candidates;
    }


    @Cacheable( // 캐싱 데이터 사용
            value = VideoLinkRedisKey.CACHE_RECOMMEND_STUDY,
            key = "#studyId + ':' + #memberId"
    )
    @Override
    @Transactional(readOnly = true)
    public List<VideoLinkDto.Row> recommendForStudy(Long studyId, Long memberId) {
        return doRecommendForStudy(studyId, memberId);
    }


    @CachePut( // 기존 Cache 덮어 쓰기
            value = VideoLinkRedisKey.CACHE_RECOMMEND_STUDY,
            key = "#studyId + ':' + #memberId"
    )
    @Override
    public List<VideoLinkDto.Row> retryRecommendForStudy(Long studyId, Long memberId) {
        return doRecommendForStudy(studyId, memberId);
    }


    // 페이지 홈에 추천할 영상 조회
    private List<VideoLinkDto.Row> doRecommendForStudy(Long studyId, Long memberId) {

        // [1] 현재 대상 학습정보 조회
        Study study = studyRepository
                .findById(studyId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));

        // [2] 이전 추천영상번호 조회 및 학습 임베딩 배열 조회
        List<Long> latestVideoLinkIds = videoLinkRedisStorage.getLatestRecommendedIds(memberId);
        log.warn("latestVideoLinkIds = {}", latestVideoLinkIds);
        byte[] embedding = study.getEmbedding();

        // [3] 유사도 기반 검색 수행
        Map<Long, VideoLinkDto.Row> candidates = videoLinkRepository
                .findSimilarWithThreshold(embedding, RECOMMEND_AMOUNT_REQUEST, RECOMMEND_THRESHOLD)
                .stream()
                .map(VideoLinkDtoMapper::toRow)
                .collect(Collectors.toConcurrentMap(
                        VideoLinkDto.Row::getVideoLinkId,
                        Function.identity()
                ));

        // [4] 영상 추천 수행 및 결과 반환
        return candidates.isEmpty() ? List.of() : doRecommend(memberId, study, candidates, latestVideoLinkIds);
    }


    // 추천 수행
    private List<VideoLinkDto.Row> doRecommend(Long memberId, Study study, Map<Long, VideoLinkDto.Row> candidates, List<Long> latestVideoLinkIds) {

        String json = StrUtils.toJson(
                VideoLinkDtoMapper.toRecommendation(study, candidates.values(), latestVideoLinkIds)
        );
        log.warn("AI REQUEST JSON : {}", json);

        // 프롬포트 생성
        String prompt = StrUtils.fillPlaceholder( // 프롬포트 생성
                VideoLinkPrompt.PROMPT_RECOMMEND_VIDEO_STUDY,
                Map.of(VideoLinkPrompt.INPUT, json)
        );

        // 추천 수행 및 결과 반환
        return AiCodeTemplate.recommendWithPrev(
                chatProvider, prompt, candidates, RECOMMEND_AMOUNT_RESPONSE,
                result -> videoLinkRedisStorage.storeLatestRecommendedIds(result.stream().map(String::valueOf).toList(), memberId)
        );

            /*
            List<Long> resultIds = AiCodeTemplate.sendQueryAndGetJson(json, prompt, );

            // 만약 6개 미만으로 추천된 경우 (AI가 없는 ID를 추천한 경우)
            List<Long> validIds = resultIds.stream()
                    .filter(candidates::containsKey)
                    .distinct()
                    .collect(Collectors.toList());

            // 부족한 수 만큼 채워 넣음
            if (validIds.size() < 6) {
                List<Long> finalValidIds = validIds;
                List<Long> additional = candidates.keySet().stream()
                        .filter(id -> !finalValidIds.contains(id))
                        .limit(RECOMMEND_AMOUNT_RESPONSE - validIds.size())
                        .toList();

                validIds = Stream.concat(validIds.stream(), additional.stream()).collect(Collectors.toList());
                LogUtils.showError(this.getClass(), "AI 분석 결과 부족분 발생. 보충 정보: %s", additional);
            }

            // 추천 결과 Id 정보 저장
            List<String> strIds = validIds.stream().map(String::valueOf).toList();
            videoLinkRedisStorage.storeLatestRecommendedIds(strIds, memberId);

            // 결과 아이디 기반 후보 Map 내에서 추출 후 반환
            Collections.shuffle(validIds); // 순서 섞기
            return validIds.stream()
                    .map(candidates::get)
                    .toList();

            // AI가 JSON 이외의 문자열을 반환하는 등 예기치 않은 반환으로 실패
        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "AI 분석 실패. 유사도 분석 상위 6개 반환");
            return candidates.values().stream().limit(6).toList();
        }

             */
    }

}
