package app.finup.layer.domain.news.utils;

import app.finup.api.external.news.dto.NewsApi;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.regex.Pattern;
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
    private static final double PERCENT_BULLET_LINES = 0.6; // 전체 라인에서 "-"로 시작하는 비율
    private static final double PERCENT_SHORT_LINES = 0.6; // 전체 라인에서 "-"로 시작하는 비율
    private static final int THRESHOLD_DESCRIPTION_MIN_LENGTH = 400; // 기사 본문 최소 길이

    private static final List<Pattern> NOISE_PATTERNS = List.of(
            Pattern.compile(".*기사보내기.*"),  // 기사보내기 포함된 모든 줄
            Pattern.compile(".*보도자료.*"),  // 기사보내기 포함된 모든 줄
            Pattern.compile("복사하기"),
            Pattern.compile(".*기사스크랩.*"),
            Pattern.compile("다른 공유 찾기"),
            Pattern.compile("현재위치"),
            Pattern.compile("입력\\s+\\d{4}\\.\\d{2}\\.\\d{2}.*"),
            Pattern.compile("댓글\\s+\\d+"),
            Pattern.compile("인쇄"),
            Pattern.compile("키워드"),
            Pattern.compile("관련 기사"),
            Pattern.compile("본문 글씨.*"),
            Pattern.compile(".*기사.*공유.*|.*공유.*기사.*"),
            Pattern.compile("^닫기$"),
            Pattern.compile("Copyright.*reserved", Pattern.CASE_INSENSITIVE),
            Pattern.compile("무단.*금지")
    );

    /**
     * 뉴스 필터링 수행
     * @param rows 뉴스 API에서 검색된 뉴스 정보 (크롤링 본문 포함)
     * @param filterKeywords 필터링할 키워드 목록
     * @return 필터링된 뉴스 DTO 목록
     */
    public static List<NewsApi.Row> filter(List<NewsApi.Row> rows, Collection<String> filterKeywords) {

        // [1] 기사 링크 중복 제거
        rows = filterDistinctLink(rows);

        // [2] 유사한 기사 제목 제거
        rows = rows.stream()
                .filter(NewsFilterUtils::isNotShort)
                .filter(row -> NewsFilterUtils.isNotTrivial(row, filterKeywords))
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
    private static boolean isNotTrivial(NewsApi.Row row, Collection<String> filterKeywords) {
        return filterKeywords.stream().noneMatch(row.getTitle()::contains);
    }



    /**
     * 뉴스 본문 필터를 위한 검증 (크롤링 본문)
     * @param description 크롤링한 뉴스 본문
     * @return 필터링된 뉴스 DTO 목록
     */
    public static boolean isDescriptionValid(String description) {

        // [1] 본문 길이가 일정 길이 미만이면 필터링
        if (description.trim().length() < THRESHOLD_DESCRIPTION_MIN_LENGTH) return false;

        // [2] bullet point 비율 체크 (-)
        // 머릿말로만 이루어진 기사 필터링
        List<String> lines = Arrays.asList(description.split("\n"));
        long bulletLines = lines.stream().filter(line -> line.trim().startsWith("-")).count();
        if (!lines.isEmpty() && (double) bulletLines / lines.size() > PERCENT_BULLET_LINES) return false;

        // [3] 짧은 문장 나열형 체크 (추가)
        long shortLines = lines.stream()
                .filter(line -> !line.trim().isEmpty() && line.trim().length() < 30)
                .count();

        // 70% 이상이 30자 미만 짧은 문장이면 제외
        if ((double) shortLines / lines.size() > PERCENT_SHORT_LINES) return false;

        // 모든 검증을 마친 경우 true
        return true;
    }


    // 짧은 연속된 줄만 있는 경우 제거
    public static String removeDescriptionNoiseLine(String description) {

        // 노이즈를 제거할 수 없는 문자열이면 빈 문자열 반환
        if (description == null || description.isEmpty()) return ""; // 제거를 수행할 수 없으면 빈 문자열 반환

        // 결과 텍스트를 담을 문자열 정의 (초기: 원래 본문)
        String result = description;

        // 연속되는 짧은 줄 제거
        result = removeShortLineClusters(result, 30, 3);

        // 패턴 매칭되는 부분만 제거
        for (Pattern pattern : NOISE_PATTERNS)
            result = pattern.matcher(result).replaceAll("");

        // 결과 문자열 반환
        return result;
    }


    // 연속된 짧은 줄 제거 (핵심 로직)
    private static String removeShortLineClusters(String text, int maxLength, int clusterSize) {

        // 문장 분리
        String[] lines = text.split("\n", -1);  // -1: 빈 문자열도 유지
        List<String> result = new ArrayList<>();

        // 문장 총 라인만큼 제거 수행
        int i = 0;
        while (i < lines.length) {

            // 현재 위치부터 연속된 짧은 줄 카운트
            int shortCount = 0;
            int j = i;

            //
            while (j < lines.length) {
                String currentTrimmed = lines[j].trim();

                // 빈 줄은 건너뛰고 계속 진행
                if (currentTrimmed.isEmpty()) {
                    j++;
                    continue;
                }

                // 짧은 줄이 등장하면 카운트
                if (currentTrimmed.length() < maxLength) {
                    shortCount++;
                    j++;

                    // 긴 줄을 만나면 중단
                } else {
                    break;
                }
            }

            // 연속된 짧은 줄이 clusterSize 이상이면 모두 제거
            if (shortCount >= clusterSize) {
                i = j;  // 짧은 줄들 건너뛰기

                // 정상적인 line 이면 결과에 문장 추가
            } else {
                result.add(lines[i]);
                i++;
            }
        }

        // 필터링 결과를 줄바꿈을 기준으로 통합 및 반환
        return String.join("\n", result);
    }
}
