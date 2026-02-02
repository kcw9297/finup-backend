package app.finup.api.external.news.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 뉴스 조회 API 결과를 매핑하기 위한 Mapper 클래스
 * @author kcw
 * @since 2025-12-24
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsApiDtoMapper {

    public static List<NewsApi.Row> toRows(NewsApi.SearchRp rp) {

        // 조회된 뉴스들
        List<NewsApi.SearchRp.Item> items = rp.getItems();

        // 각 뉴스별 DTO 변환 후 반환
        return items.stream()
                .map(item -> NewsApi.Row.builder()
                        .title(item.getTitle())
                        .summary(item.getDescription())
                        .link(item.getLink())
                        .publishedAt(ZonedDateTime.parse(item.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDateTime())
                        .build())
                .collect(Collectors.toMap(
                        NewsApi.Row::getTitle,  // key: 제목
                        Function.identity(),    // value: Row 자체
                        (existing, replacement) -> existing // 중복 시 먼저 나온 것 유지 (중복 제거)
                ))
                .values() // 중복 제거 후 ROW만 추출하여 반환
                .stream()
                .toList();
    }
}
