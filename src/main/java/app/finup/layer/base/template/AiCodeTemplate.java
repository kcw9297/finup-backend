package app.finup.layer.base.template;

import app.finup.common.utils.AiUtils;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.ChatProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AiCodeTemplate {

    /**
     * 이전 데이터 기반 "AI 분석 작업" 수행
     * @param chatProvider ChatProvider Bean
     * @param prompt AI 프롬포트
     * @param savePrevMethod 이전 결과를 저장할 메소드(함수)
     * @return T 응답 클래스 형태로 역직렬화된 AI 분석 결과
     */
    public static <T> T analyzeWithPrev(
            ChatProvider chatProvider,
            String prompt,
            Consumer<T> savePrevMethod) {

        // [1] 쿼리 전달
        String response = chatProvider.query(prompt);
        String clean = AiUtils.removeMarkdown(response); // 마크다운 등 불필요 문자 제거

        // [2] 이전 결과 저장
        T result = StrUtils.fromJson(clean, new TypeReference<>() {});
        savePrevMethod.accept(result);

        // [3] 결과 반환
        return result;
    }


    /**
     * 이전 데이터 기반 "AI 추천 작업" 수행
     * @param chatProvider ChatProvider Bean
     * @param prompt AI 프롬포트
     * @param candidates 추천 후보 Map (Map<고유ID, 데이터> 형태)
     * @param minRecommendAmount 추천 최소 개수
     * @param savePrevMethod 이전 결과를 저장할 메소드(함수)
     * @return T 응답 클래스 형태로 역직렬화된 AI 분석 결과
     */
    public static <K, V> List<V> recommendWithPrev(
            ChatProvider chatProvider,
            String prompt,
            Map<K, V> candidates,
            int minRecommendAmount,
            Consumer<List<K>> savePrevMethod) {

        // [1] 쿼리 전달
        String response = chatProvider.query(prompt);
        String clean = AiUtils.removeMarkdown(response); // 마크다운 등 불필요 문자 제거

        // [2] 추천 결과 확인
        List<K> recommendIds = StrUtils.fromJson(clean, new TypeReference<>() {});
        LogUtils.showInfo(AiCodeTemplate.class, "📊 AI 추천 결과 : %s", recommendIds);

        // [5] 추천 결과 검증
        // 5-1) 유효한 영상번호만 추출 (AI가 목록 외 번호를 추천한 경우 필터)
        List<K> validIds = recommendIds.stream()
                .filter(candidates::containsKey)
                .distinct()
                .collect(Collectors.toList());

        // 5-3) 만약 일정 개수 미만으로 추천된 경우, 기존 후보 영상에서 넣음
        if (validIds.size() < minRecommendAmount) {
            List<K> finalValidIds = validIds;
            List<K> additional = candidates.keySet().stream()
                    .filter(id -> !finalValidIds.contains(id))
                    .limit(minRecommendAmount - validIds.size())
                    .toList();

            // 유효 id와 새롭게 보강한 id 를 합침
            validIds = Stream.concat(validIds.stream(), additional.stream()).collect(Collectors.toList());
            LogUtils.showWarn(AiCodeTemplate.class, "AI 분석 결과 부족분 발생. 보충 정보: %s", additional);
        }

        // [6] 추천 결과 Id 정보 저장
        savePrevMethod.accept(validIds);

        // [7] 결과 아이디 기반 후보 Map 내에서 추출 후 반환
        Collections.shuffle(validIds); // 순서 섞기
        return validIds.stream()
                .map(candidates::get)
                .filter(Objects::nonNull)
                .toList();
    }


}
