package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NewsRemoveDuplicateService 구현 클래스
 * @author oyh
 * @since 2025-12-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsRemoveDuplicateServiceImpl implements NewsRemoveDuplicateService {
    @Override
    public List<NewsDto.Row> removeDuplicate(List<NewsDto.Row> list) {

        list = distinctByUrl(list);

        list = removeByTitleSimilarity(list);

        list = list.stream()
                .filter(row -> !isTooShort(row))
                .filter(row -> !isPriceOnlyNews(row))
                .filter(row -> !isTrivialMention(row))
                .toList();
        return list;
    }
    //url 기준 중복 제거
    private List<NewsDto.Row> distinctByUrl(List<NewsDto.Row> list) {
        Set<String> seen = new HashSet<>();
        return list.stream()
                .filter(item -> seen.add(item.getLink()))
                .toList();
    }


    //제목 유사도 중복 제거
    private List<NewsDto.Row> removeByTitleSimilarity(List<NewsDto.Row> list) {
        List<NewsDto.Row> result = new ArrayList<>();
        for (NewsDto.Row row : list) {
            boolean isDuplicate = result.stream().anyMatch(existing -> {
                double j = jaccard(existing.getTitle(), row.getTitle());

                // 의미 거의 동일 → 바로 컷
                if (j >= 0.6) return true;

                //  단어 일부만 겹치지만 표현이 거의 동일
                if (j >= 0.4) {
                    double l = similarity(existing.getTitle(), row.getTitle());
                    return l >= 0.75;
                }

                return false;
            });

            if (!isDuplicate) {
                result.add(row);
            }
        }
        return result;
    }

    private Set<String> tokenize(String s) {
        return Arrays.stream(
                        s.replaceAll("[^가-힣0-9 ]", " ")
                                .split("\\s+")
                )
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet()); // 중복 자동 제거
    }

    private double jaccard(String a, String b) {
        Set<String> s1 = tokenize(a);
        Set<String> s2 = tokenize(b);

        Set<String> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);

        Set<String> union = new HashSet<>(s1);
        union.addAll(s2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private double similarity(String s1, String s2) {
        LevenshteinDistance ld = new LevenshteinDistance();
        int distance = ld.apply(s1, s2);

        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;

        return 1.0 - ((double) distance / maxLen); // 0.0 ~ 1.0 사이 값
    }
    private boolean isTooShort(NewsDto.Row row) {
        return row.getTitle() == null
                || row.getTitle().length() < 15
                || row.getDescription() == null
                || row.getDescription().length() < 30;
    }

    private static final List<String> PRICE_ONLY_KEYWORDS = List.of(
            "상장","퀴즈","금시세","소개","챌린지","이벤트"
    );

    private boolean isPriceOnlyNews(NewsDto.Row row) {
        String title = row.getTitle();
        return PRICE_ONLY_KEYWORDS.stream().anyMatch(title::contains);
    }

    private boolean isTrivialMention(NewsDto.Row row) {
        return row.getTitle().contains("언급")
                || row.getTitle().contains("관련")
                || row.getTitle().contains("거론");
    }
}
