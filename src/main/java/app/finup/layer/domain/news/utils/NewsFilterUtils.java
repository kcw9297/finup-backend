package app.finup.layer.domain.news.utils;

import app.finup.common.utils.HtmlUtils;
import app.finup.common.utils.StrUtils;
import app.finup.layer.domain.news.support.NewsObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 뉴스 필터링 관련 유틸 기능 클래스
 * @author kcw
 * @since 2026-01-07
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsFilterUtils {

    // 제공 상수
    public static final Set<String> FILTER_KEYWORD_MAIN = Set.of(
            "마감 시황", "개장 시황", "출발 시황", "보합 출발", "혼조세",
            "장중 시황", "장중 등락", "오늘의 증시", "이 시각 증시", "지금 증시",
            "상한가 행진", "상한가 종목", "폭등", "급등주",
            "속출", "고수", "부자", "대박", "리치", "지금 사야", "매수 타이밍", "놓치면",
            "테마", "특징주", "고배율",
            "골든크로스", "데드크로스", "분배금",
            "암호화폐", "코인", "비트코인", "부동산", "외환", "금시세", "카드론",
            "정당", "지지도", "선거", "총선", "교회",
            "[속보]", "[영상]", "퀴즈", "챌린지", "이벤트", "당첨",
            "연예", "스포츠", "축구", "야구", "공연", "음악",
            "역사", "문화재", "전시", "투어",
            "株", "社", "포인트", "고용"
    );

    public static final Set<String> FILTER_KEYWORD_STOCK = Set.of(
            "마감", "개장", "출발", "보합", "혼조", "장중", "매수", "폭등세", "매도", "장 마감",
            "급등주", "간다", "오늘의 주식", "이 시각 증시", "체크", "점검",
            "속출", "고수", "부자", "대박", "리치",
            "테마주", "특징주", "고배율", "추천주", "관심주", "지금 사야", "매수 타이밍", "놓치면",
            "골든크로스", "데드크로스", "분배금",
            "부동산", "외환", "금시세", "카드론",
            "정당", "지지도", "선거", "총선", "교회",
            "[속보]", "[영상]", "퀴즈", "챌린지", "이벤트", "당첨"
    );

    // 정확히 일치하는 제외 단어
    public static final Set<String> FILTER_ANALYSIS_WORDS = Set.of(
            // 가격/거래
            "주가", "주식", "매수", "매도", "거래", "거래량", "시세",
            // 투자 주체
            "개인투자자", "기관투자자", "외국인투자자", "개인", "기관", "외국인",
            // 기초 지표
            "환율", "금리", "코스피", "코스닥", "시가총액", "시총",
            // 기초 개념
            "실적", "수익", "손실", "이익", "차익", "배당", "투자", "수익률", "허가", "자산", "글로벌",
            "신뢰도", "기대", "변동성", "매출", "매출액", "영업이익", "순이익", "국내시장", "해외시장",
            // 산업명
            "반도체", "AI", "전기차", "바이오", "it", "테크", "내수",
            // 방향성/상태
            "급등", "급락", "상승", "하락", "증가", "감소", "확대", "축소",
            "호조", "부진", "성장", "둔화", "폭등", "폭락", "충격",
            // 감정/심리
            "기대감", "피로감", "불안감", "부담감", "안도감",
            "강세", "약세", "상승세", "하락세",
            // 기초 투자 행동
            "저가 매수", "고가 매도", "분할 매수", "차익 실현", "이익 실현",
            // 비즈니스 일반
            "생산능력", "생산라인", "설비투자", "고객사", "공급사", "수주", "납품"
    );

    // 포함되면 제외할 패턴 (복합어 대응)
    public static final Set<String> FILTER_ANALYSIS_WORDS_PATTERNS = Set.of(
            "국면",
            "효과",
            "장세",
            "심리",
            "투자자",
            "경쟁력",
            "거래시간"
    );



    // 사용 상수
    private static final int N_GRAM = 3;
    private static final double THRESHOLD_JACCARD_TITLE = 0.55;
    private static final double THRESHOLD_JACCARD_DESCRIPTION = 0.35;
    private static final double THRESHOLD_DICE_TITLE = 0.68;
    private static final double THRESHOLD_DICE_DESCRIPTION = 0.50;

    private static final int THRESHOLD_LENGTH_MIN_TITLE = 20;
    private static final int THRESHOLD_LENGTH_MIN_SUMMARY = 50;
    private static final double PERCENT_BULLET_LINES = 0.6; // 전체 라인에서 "-"로 시작하는 비율
    private static final double PERCENT_SHORT_LINES = 0.6; // 전체 라인에서 "-"로 시작하는 비율
    private static final int THRESHOLD_DESCRIPTION_MIN_LENGTH = 1500; // 기사 본문 최소 길이

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
     * 뉴스 제목 심층 필터링 수행
     * @param targetNews 유사도를 판별할 뉴스 목록
     * @param curNews 이미 저장된 기존 뉴스
     * @param filterKeywords 필터링할 키워드 목록
     * @return 필터링된 뉴스 DTO 목록
     */
    public static <T extends NewsObject, C extends NewsObject> List<T> filterDetailTitle(
            Collection<T> targetNews, Collection<C> curNews, Collection<String> filterKeywords
    ) {

        // [1] 기사 링크 중복 제거
        targetNews = filterDistinctLink(targetNews);

        // [2] 유사한 기사 제목 제거
        targetNews = targetNews.stream()
                .filter(NewsFilterUtils::isNotShort)
                .filter(NewsFilterUtils::isValid)
                .filter(row -> NewsFilterUtils.isNotTrivial(row, filterKeywords))
                .toList();

        // [3] 제목 유사도 검사 후 결과 반환
        return filterSimilarTitle(targetNews, curNews, false);
    }


    // 기사 정보가  정상적으로 제공되었는지 검증
    private static <T extends NewsObject> boolean isValid(T row) {

        return StrUtils.isValid(row.getLink()) &&
                StrUtils.isValid(row.getTitle()) &&
                StrUtils.isValid(row.getSummary());
    }

    // 문자열, 요약 정보가 없거나, 너무 짧지 않은지 검증
    private static <T extends NewsObject> boolean isNotShort(T row) {

        // [1] 검증 대상
        String title = row.getTitle();
        String summary = row.getSummary();

        // [2] 길이 비교 및 결과 반환
        return Objects.nonNull(title) && Objects.nonNull(summary) &&
                title.length() >= THRESHOLD_LENGTH_MIN_TITLE &&
                summary.length() >= THRESHOLD_LENGTH_MIN_SUMMARY;
    }


    // 불필요한 단어가 포함되어 있는지 검증
    private static <T extends NewsObject> boolean isNotTrivial(T row, Collection<String> filterKeywords) {
        return filterKeywords.stream().noneMatch(row.getTitle()::contains);
    }


    // 기사 링크 필터링 (중복 제거)
    private static <T extends NewsObject> List<T> filterDistinctLink(Collection<T> rows) {

        // [1] 중복을 불허하는 set Collection 생성
        Set<String> seen = new HashSet<>();

        // [2] 중복된 링크를 삽입하는 경우, add 결과는 false (삽입 성공한 결과만 필터링)
        return rows.stream()
                .filter(row -> seen.add(row.getLink()))
                .toList();
    }


    /**
     * 유사한 기사 제목 제거
     * @param <T> NewsObject 구현 뉴스 객체
     * @param targetNews 유사도를 판별할 뉴스 목록
     * @param curNews 이미 저장된 기존 뉴스
     * @param returnSimilar 필터된 결과를 받는지 여부 (true - 필터에 "걸린" 유사 기사 목록을 제공)
     * @return 필터링된 기사 목록
     */
    public static <T extends NewsObject, C extends NewsObject> List<T> filterSimilarTitle(
            Collection<T> targetNews, Collection<C> curNews, boolean returnSimilar
    ) {
        return filterSimilar(targetNews, curNews, true, false, returnSimilar);
    }


    /**
     * 유사한 기사 본문 제거
     * @param <T> NewsObject 구현 뉴스 객체
     * @param targetNews 유사도를 판별할 뉴스 목록
     * @param curNews 이미 저장된 기존 뉴스
     * @param returnSimilar 필터된 결과를 받는지 여부 (true - 필터에 "걸린" 유사 기사 목록을 제공)
     * @return 필터링된 기사 목록
     */
    public static <T extends NewsObject, C extends NewsObject> List<T> filterSimilarDescription(
            Collection<T> targetNews, Collection<C> curNews, boolean returnSimilar
    ) {
        return filterSimilar(targetNews, curNews, false, true, returnSimilar);
    }


    // 유사도 검증 수행
    public static <T extends NewsObject, C extends NewsObject> List<T> filterSimilar(
            Collection<T> targetNews,
            Collection<C> curNews,
            boolean isTitle,
            boolean isDescription,
            boolean returnSimilar
    ) {

        // [1] 결과를 담을 리스트 선언
        List<NewsObject> allProcessed = new ArrayList<>(curNews); // 이미 존재하는 뉴스 담음
        List<T> result = new ArrayList<>();
        List<T> filteredResult = new ArrayList<>();

        // [2] 유사 판별 수행 및 결과 수집
        for (T news : targetNews) {

            // 유사 판별 수행
            boolean isTitleSimilar = isTitle &&
                    isSimilar(news, allProcessed, NewsObject::getTitle, isTitle, isDescription);
            boolean isDescriptionSimilar = isDescription &&
                    isSimilar(news, allProcessed, NewsObject::getDescription, isTitle, isDescription);

            // 결과 저장
            if (isTitle && !isDescription)
                addToResult(isTitleSimilar, returnSimilar, news, filteredResult, result, allProcessed);

            else if (!isTitle && isDescription)
                addToResult(isDescriptionSimilar, returnSimilar, news, filteredResult, result, allProcessed);
        }

        // [3] 결과 저장
        return returnSimilar ? filteredResult : result;
    }


    // 필터 결과 삽입
    private static <T extends NewsObject> void addToResult(
            boolean isSimilar, boolean returnSimilar, T news, List<T> filteredResult, List<T> result, List<NewsObject> allProcessed
    ) {
        if (isSimilar && returnSimilar) filteredResult.add(news); // "필터당한" 결과를 반환하는 경우 (유사한 기사들 목록)
        else if (!isSimilar && !returnSimilar) result.add(news); // "필터링" 된 유사 기사가 없는 정보만 반환하는 경우
        allProcessed.add(news);
    }


    // 제목 유사 여부 판단
    private static <T extends NewsObject> boolean isSimilar(
            T news,
            List<NewsObject> allProcessed,
            Function<NewsObject, String> extractor,
            boolean isTitle,
            boolean isDescription
    ) {

        // [1] 검사 대상 뉴스 정보 일반화
        String s1 = normalize(extractor.apply(news));
        if (s1.isEmpty()) return false; // 빈 단어이면 유사도 검사 미수행

        // [2] 검사 대상 토큰화
        Set<String> grams1 = ngrams(s1);

        // [3] 이미 통과한 뉴스들(result 내 데이터)과 유사한지 검증
        return allProcessed.stream()
                .anyMatch(existing -> {

                    // 결과 목록 뉴스 일반화
                    String s2 = normalize(extractor.apply(existing));

                    // 제목이 완전히 같으면 중복으로 간주 (검증 유형 무관)
                    if (Objects.equals(s1, s2)) return true;
                    if (s2.isEmpty()) return false; // 혹시라도 빈 결과인 경우 통과

                    // 결과 목록 뉴스 정보 토큰화
                    Set<String> grams2 = ngrams(s2);

                    //  검사 수행
                    if (isTitle && !isDescription)
                        return dice(grams1, grams2) >= THRESHOLD_DICE_TITLE;

                    if (!isTitle && isDescription)
                        return jaccard(grams1, grams2) >= THRESHOLD_JACCARD_DESCRIPTION || dice(grams1, grams2) >= THRESHOLD_DICE_DESCRIPTION;

                    // 유효하지 않은 기준인 경우 검사 미수행
                    return false;
                });
    }


    // 텍스트 일반화
    private static String normalize(String text) {

        return Objects.isNull(text) ?
                "" :
                HtmlUtils.getText(text)
                        .toLowerCase()
                        // 괄호 안 내용 제거 (영문명, 설명 등)
                        .replaceAll("\\([^)]*\\)", "")
                        .replaceAll("\\[[^]]*\\]", "")

                        // 연속된 숫자를 하나의 토큰으로 (퍼센트, 배수 등)
                        .replaceAll("\\d+\\.?\\d*%?배?조?억?만?원?", "숫자")

                        // 공백 제거
                        .replaceAll("\\s+", "")

                        // 한글, 영문, 숫자만 남김
                        .replaceAll("[^가-힣a-z0-9]", "");
    }


    // N-GRAM (N글자 기준으로 단어 쪼갬. 3글자를 기준으로 토큰화와 유사한 것)
    private static Set<String> ngrams(String s) {

        Set<String> grams = new HashSet<>();
        if (s.length() < N_GRAM) return grams;

        for (int i = 0; i <= s.length() - N_GRAM; i++) {
            grams.add(s.substring(i, i + N_GRAM));
        }
        return grams;
    }


    // JACCARD 검사 : 합집합 계산
    private static double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 1.0;

        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        Set<String> union = new HashSet<>(a);
        union.addAll(b);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }


    // DICE 계산 : 얼마나 단어 패턴이 겹치는가 (교집합 중심 검사)
    private static double dice(Set<String> a, Set<String> b) {

        if (a.isEmpty() && b.isEmpty()) return 1.0;

        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        return (2.0 * intersection.size()) / (a.size() + b.size());
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
        result = removeShortLineClusters(result, 50, 3);

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


    /**
     * 필터 단어가 아닌지 판별
     * @param wordName 대상 단어명
     * @return 정상 용어이면 true, 필터 대상이면 false
     */
    public static boolean isNotFilteredWord(String wordName) {

        // 비교 대상이 NULL이거나 공백인 경우는 true
        if (Objects.isNull(wordName) || wordName.isBlank()) return false;

        // 양끝 공백을 제거하고 소문자로 변환
        String normalized = wordName.trim().toUpperCase();

        // 비교 수행
        return !FILTER_ANALYSIS_WORDS.contains(normalized) &&
                FILTER_ANALYSIS_WORDS_PATTERNS.stream().noneMatch(normalized::contains);
    }
}
