package app.finup.layer.domain.studyword.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.study.repository.StudyRepository;
import app.finup.layer.domain.studyword.constant.StudyWordCache;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import app.finup.layer.domain.studyword.dto.StudyWordDtoMapper;
import app.finup.layer.domain.studyword.manager.StudyWordAiManager;
import app.finup.layer.domain.studyword.redis.StudyWordRedisStorage;
import app.finup.layer.domain.studyword.repository.StudyWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyWordAiServiceImpl implements StudyWordAiService {

    // 사용 의존성
    private final StudyWordRepository studyWordRepository;
    private final StudyRepository studyRepository;
    private final StudyWordAiManager studyWordAiManager;
    private final StudyWordRedisStorage studyWordRedisStorage;
    private final FileStorage fileStorage;

    // 사용 상수
    private static final int RECOMMEND_AMOUNT_REQUEST = 20; // DB에 요청하는 추천 단어 개수
    private static final int RECOMMEND_AMOUNT_RESPONSE = 6; // 실제로 반환하는 추천 단어 개수

    @Cacheable( // 캐싱 데이터 사용
            value = StudyWordCache.RECOMMEND_STUDY,
            key = "#memberId + ':' + #studyId"
    )
    @Override
    public List<StudyWordDto.Row> recommendForStudy(Long memberId, Long studyId) {
        return recommendStudyWord(memberId, studyId);
    }

    @CachePut( // 캐싱 데이터 사용
            value = StudyWordCache.RECOMMEND_STUDY,
            key = "#memberId + ':' + #studyId"
    )
    @Override
    public List<StudyWordDto.Row> retryRecommendForStudy(Long memberId, Long studyId) {
        return recommendStudyWord(memberId, studyId);
    }


    private List<StudyWordDto.Row> recommendStudyWord(Long memberId, Long studyId) {

        // [1] 대상 학습정보 조회
        Study study = studyRepository
                .findById(studyId)
                .orElseThrow(() -> new BusinessException(AppStatus.STUDY_NOT_FOUND));

        // [2] 이전 추천단어번호 조회 및 학습 임베딩 배열 조회
        List<Long> latestStudyWordIds = studyWordRedisStorage.getLatestRecommendedIds(memberId);
        log.warn("latestStudyWordIds = {}", latestStudyWordIds);
        byte[] embedding = study.getEmbedding();

        // [3] 유사도 기반 검색 수행
        Map<Long, StudyWordDto.Row> candidates = studyWordRepository
                .findSimilar(embedding, RECOMMEND_AMOUNT_REQUEST)
                .stream()
                .map(StudyWordDtoMapper::toRow)
                .collect(Collectors.toConcurrentMap(
                        StudyWordDto.Row::getStudyWordId,
                        Function.identity()
                ));

        // [4] 단어 추천 수행 및 결과 반환
        return candidates.isEmpty() ? List.of() : doRecommend(memberId, study, candidates, latestStudyWordIds);
    }


    // 추천 수행
    private List<StudyWordDto.Row> doRecommend(Long memberId, Study study, Map<Long, StudyWordDto.Row> candidates, List<Long> latestStudyWordIds) {


        try {
            // 요청 DTO 생성 후, JSON 변환
            String json = StrUtils.toJson(StudyWordDtoMapper.toRecommendation(study, candidates.values(), latestStudyWordIds));
            log.warn("AI REQUEST JSON : {}", json);

            // 추천 수행
            List<Long> resultIds = studyWordAiManager.recommendForStudy(json);

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
            studyWordRedisStorage.storeLatestRecommendedIds(strIds, memberId);

            // 결과 아이디 기반 후보 Map 내에서 추출 후 반환
            Collections.shuffle(validIds); // 순서 섞기
            return validIds.stream()
                    .map(candidates::get)
                    .map(this::enrichImageUrl)
                    .toList();


            // AI가 JSON 이외의 문자열을 반환하는 등 예기치 않은 반환으로 실패
        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "AI 분석 실패. 유사도 분석 상위 6개 반환");
            return candidates.values().stream().limit(6)
                    .map(this::enrichImageUrl)
                    .toList();
        }
    }
    
    // StudyWordDto.Row ImageUrl 가공 전용 메소드
    private StudyWordDto.Row enrichImageUrl(StudyWordDto.Row row) {
        if (row.getImageUrl() == null) {
            return row;
        }

        return row.toBuilder()
                .imageUrl(fileStorage.getUrl(row.getImageUrl()))
                .build();
    }
}
