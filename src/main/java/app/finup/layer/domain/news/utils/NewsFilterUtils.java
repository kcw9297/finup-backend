package app.finup.layer.domain.news.utils;

import app.finup.infra.api.news.dto.NewsApi;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 뉴스 필터링 관련 유틸 기능 클래스
 * @author kcw
 * @since 2026-01-07
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsFilterUtils {

    // 사용 상수
    private static final LevenshteinDistance LD = new LevenshteinDistance();
    private static final double THRESHOLD_JACCARD_HIGH = 0.6;
    private static final double THRESHOLD_JACCARD_MIDDLE = 0.4;
    private static final double THRESHOLD_SIMILARITY = 0.75;
    private static final int THRESHOLD_LENGTH_MIN_TITLE = 15;
    private static final int THRESHOLD_LENGTH_MIN_SUMMARY = 50;
    private static final List<String> KEYWORDS_TRIVIAL = List.of("상장", "퀴즈", "금시세", "소개", "챌린지", "이벤트", "언급", "관련", "거론");


    /**
     * 뉴스 필터링 수행
     * @param rows 뉴스 API에서 검색된 뉴스 정보 (크롤링 본문 포함)
     * @return 필터링된 뉴스 DTO 목록
     */
    public static List<NewsApi.Row> filter(List<NewsApi.Row> rows) {

        // [1] 기사 링크 중복 제거
        rows = filterDistinctLink(rows);

        // [2] 유사한 기사 제목 제거
        rows = rows.stream()
                .filter(NewsFilterUtils::isNotShort)
                .filter(NewsFilterUtils::isNotTrivial)
                .toList();

        // [3] 제목 유사도 검사 후 결과 반환
        return filterSimilarTitle(rows);
    }


    // 기사 링크 필터링 (중복 제거)
    private static List<NewsApi.Row> filterDistinctLink(List<NewsApi.Row> rows) {

        // [1] 중복을 불허하는 set Collection 생성
        Set<String> seen = new HashSet<>();

        // [2] 중복된 링크를 삽입하는 경우, add 결과는 false (삽입 성공한 결과만 필터링)
        return rows.stream()
                .filter(row -> seen.add(row.getLink()))
                .toList();
    }


    // 제목 유사도 중복 제거
    private static List<NewsApi.Row> filterSimilarTitle(List<NewsApi.Row> rows) {

        // [1] 결과를 담을 result List 선언
        List<NewsApi.Row> result = new ArrayList<>();

        // [2] 유사도 계산 후, 검증을 통과한 목록 반환
        for (NewsApi.Row row : rows) checkSimilar(row, result);
        return result;
    }


    // 유사도 계산
    private static void checkSimilar(NewsApi.Row row, List<NewsApi.Row> result) {

        // [1] 유사 여부 검사
        boolean isSimilar = result.stream()
                .anyMatch(existing -> {
                    // [1] 제목 단어 겹침 정도 계산 (jaccard)
                    double j = calculateJaccard(existing.getTitle(), row.getTitle());

                    // [2] 유사도 판별 결과 반환
                    // 60% 이상의 중복이 발견되면 즉시 유사 판별
                    // 40% 이상의 중복인 경우, 토큰화 후 유사도를 계산하여 75% 이상 유사도를 가지면 유사 판별
                    return j >= THRESHOLD_JACCARD_HIGH ||
                            (j >= THRESHOLD_JACCARD_MIDDLE &&
                                    calculateSimilarity(existing.getTitle(), row.getTitle()) >= THRESHOLD_SIMILARITY);
                });

        // [2] 유사하지 않은 경우 result 결과에 추가
        if (!isSimilar) result.add(row);
    }


    // 중복 비율 계산
    private static double calculateJaccard(String a, String b) {

        // [1] 중복 계산을 위한 토큰화 수행
        Set<String> s1 = tokenize(a);
        Set<String> s2 = tokenize(b);

        // [2] 교집합 & 합집합 계산
        Set<String> intersection = new HashSet<>(s1); // 교집합
        intersection.retainAll(s2);

        Set<String> union = new HashSet<>(s1); // 합집합
        union.addAll(s2);

        // [3] 중복 계산
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }


    // 단어 토큰화 수행
    private static Set<String> tokenize(String s) {

        // [1] 한글, 숫자, 공백이 아닌 모든 문자는 공백으로 대체. 이후 공백을 기준으로 split
        String[] split = s.replaceAll("[^가-힣0-9 ]", " ").split("\\s+");

        // [2] 공백만 존재하는 토큰을 제거하고, 중복 제거 (set) 및 반환
        return Arrays.stream(split)
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet()); // 중복 자동 제거
    }


    // 유사도 계산
    private static double calculateSimilarity(String s1, String s2) {

        // 거리 유사도 계산
        int distance = LD.apply(s1, s2);

        // 문자열 최대 길이 계산
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0; // 만약 빈 문자열이면 최대 유시도로 취급

        // 유사도 계산 및 반환
        return 1.0 - ((double) distance / maxLen); // 0.0 ~ 1.0 사이 값
    }


    // 문자열, 요약 정보가 없거나, 너무 짧지 않은지 검증
    private static boolean isNotShort(NewsApi.Row row) {

        // [1] 검증 대상
        String title = row.getTitle();
        String summary = row.getSummary();

        // [2] 길이 비교 및 결과 반환
        return Objects.nonNull(title) && Objects.nonNull(summary) &&
                title.length() >= THRESHOLD_LENGTH_MIN_TITLE &&
                summary.length() >= THRESHOLD_LENGTH_MIN_SUMMARY;
    }


    // 불필요한 단어가 포함되어 있는지 검증
    private static boolean isNotTrivial(NewsApi.Row row) {
        return KEYWORDS_TRIVIAL.stream().noneMatch(row.getTitle()::contains);
    }
}
