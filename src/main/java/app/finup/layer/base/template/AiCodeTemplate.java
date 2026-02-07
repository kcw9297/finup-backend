package app.finup.layer.base.template;

import app.finup.common.enums.LogEmoji;
import app.finup.common.utils.AiUtils;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * AI 로직 중, 공용 코드를 제공하는 탬플릿 클래스
 * @author kcw
 * @since 2026-01-05
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AiCodeTemplate {


    /**
     * AI 쿼리 요청 후, 문자열 결과 그대로 반환
     * @param sendPromptMethod 프롬포트 전송 메소드
     * @return T 응답 클래스 형태로 역직렬화된 AI 분석 결과
     */
    public static String sendQueryAndGetString(Supplier<String> sendPromptMethod) {
        String response = sendPromptMethod.get();
        return AiUtils.removeMarkdown(response);
    }


    /**
     * AI 쿼리 요청 후, 문자열 결과 그대로 반환
     * @param sendPromptMethod 프롬포트 전송 메소드
     * @return T 응답 클래스 형태로 역직렬화된 AI 분석 결과
     */
    public static String sendQueryAndGetStringWithPrev(
            Supplier<String> sendPromptMethod,
            Consumer<String> savePrevMethod) {

        // [1] 쿼리 수행
        String response = sendPromptMethod.get();
        String clean = AiUtils.removeMarkdown(response);

        // [2] 이력 저장
        savePrevMethod.accept(clean);

        // [3] 결과 반환
        return clean;
    }



    /**
     * AI 쿼리 요청 후, JSON 결과 반환 (이전 이력)
     * @param sendPromptMethod 프롬포트 전송 메소드
     * @param dtoClass         역직렬화 할 DTO 클래스 정보
     * @return T 응답 클래스 형태로 역직렬화된 AI 분석 결과
     */
    public static <T> T sendQueryAndGetJson(
            Supplier<String> sendPromptMethod,
            Class<T> dtoClass) {

        // [1] 쿼리 전달
        String response = sendPromptMethod.get();
        String clean = AiUtils.removeMarkdown(response); // 마크다운 등 불필요 문자 제거

        // [2] 결과 반환
        return StrUtils.fromJson(clean, dtoClass);
    }


    /**
     * AI 쿼리 요청 후, JSON 결과를 저장 후 반환 (이전 이력)
     * @param dtoClass         역직렬화 할 DTO 클래스 정보
     * @param sendPromptMethod 프롬포트 전송 메소드
     * @param savePrevMethod   이전 결과를 저장할 메소드(함수)
     * @return T 응답 클래스 형태로 역직렬화된 AI 분석 결과
     */
    public static <T> T sendQueryAndGetJsonWithPrev(
            Class<T> dtoClass,
            Supplier<String> sendPromptMethod,
            Consumer<T> savePrevMethod) {

        // [1] 쿼리 전달
        String response = sendPromptMethod.get();
        String clean = AiUtils.removeMarkdown(response); // 마크다운 등 불필요 문자 제거

        // [2] 이전 결과 저장
        T result = StrUtils.fromJson(clean, dtoClass);
        savePrevMethod.accept(result);

        // [3] 결과 반환
        return result;
    }


    /**
     * 이전 데이터 기반 "AI 추천 작업" 수행
     * @param candidates            추천 후보 Map (Map<고유ID, 데이터> 형태)
     * @param candidatesKeyClass    Candidate 후보 Map Key Class (고유ID 클래스)
     * @param minRecommendAmount    추천 최소 개수
     * @param sendPromptMethod      프롬포트 전송 메소드
     * @param savePrevMethod        이전 결과를 저장할 메소드(함수)
     * @return T 응답 클래스 형태로 역직렬화된 AI 분석 결과
     */
    public static <K, V> List<V> recommendWithPrev(
            Map<K, V> candidates,
            Class<K> candidatesKeyClass,
            int minRecommendAmount,
            Supplier<String> sendPromptMethod,
            Consumer<List<K>> savePrevMethod) {

        // [1] 쿼리 전달
        String response = sendPromptMethod.get();
        String clean = AiUtils.removeMarkdown(response); // 마크다운 등 불필요 문자 제거

        // [2] 추천 결과 확인
        List<K> recommendIds = StrUtils.fromJsonList(clean, candidatesKeyClass);
        LogUtils.showInfo(AiCodeTemplate.class, LogEmoji.ANALYSIS, "AI 추천 결과 : %s", recommendIds);

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
            LogUtils.showWarn(AiCodeTemplate.class, "AI 분석 결과 부족분 발생. 보충 개수 : %d, 보충 정보: %s", additional.size(), additional);
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


    /**
     * 이전 데이터 기반 "AI 추천 작업" 수행 (후보 데이터 없음)
     * @param responseClass    추천 결과 Class 정보 (만약, 문자열 결과를 원하면 String)
     * @param sendPromptMethod 프롬포트 전송 메소드
     * @param savePrevMethod   이전 결과를 저장할 메소드(함수)
     * @return 응답 클래스 형태로 역직렬화된 AI 분석 결과
     */
    public static <T> List<T> analyzeWithPrev(
            Class<T> responseClass,
            Supplier<String> sendPromptMethod,
            Consumer<List<T>> savePrevMethod) {

        // [1] 쿼리 전달
        String response = sendPromptMethod.get();
        String clean = AiUtils.removeMarkdown(response); // 마크다운 등 불필요 문자 제거

        // [2] 추천 결과 확인
        List<T> recommendations = StrUtils.fromJsonList(clean, responseClass);
        LogUtils.showInfo(AiCodeTemplate.class, LogEmoji.ANALYSIS, "AI 추천 결과 : %s", recommendations);

        // [3] 필터 결과 저장 및 저장 수행
        savePrevMethod.accept(recommendations);

        // [5] 결과 아이디 기반 후보 Map 내에서 추출 후 반환
        Collections.shuffle(recommendations); // 순서 섞기
        return recommendations;
    }


}
