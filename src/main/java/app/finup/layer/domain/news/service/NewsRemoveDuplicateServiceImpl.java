package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        list = removeByTitleSimilarity(list, 0.85);

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
    private List<NewsDto.Row> removeByTitleSimilarity(List<NewsDto.Row> list, double sim) {
        List<NewsDto.Row> result = new ArrayList<>();
        for(NewsDto.Row row : list) {
            boolean isDuplicate = result.stream().anyMatch(existing ->
                    similarity(existing.getTitle(), row.getTitle()) > sim);
            if(!isDuplicate) {
                result.add(row);
            }
        }
        return result;
    }

    private double similarity(String s1, String s2) {
        LevenshteinDistance ld = new LevenshteinDistance();
        int distance = ld.apply(s1, s2);

        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;

        return 1.0 - ((double) distance / maxLen); // 0.0 ~ 1.0 사이 값
    }
}
