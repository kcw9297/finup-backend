package app.finup.layer.domain.indexMarket.service;

import app.finup.layer.domain.indexMarket.api.IndexMarketApiClient;
import app.finup.layer.domain.indexMarket.dto.IndexMarketDto;
import app.finup.layer.domain.indexMarket.dto.IndexMarketDtoMapper;
import app.finup.layer.domain.indexMarket.entity.IndexMarket;
import app.finup.layer.domain.indexMarket.repository.IndexMarketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexMarketServiceImpl implements IndexMarketService {
    private final IndexMarketApiClient indexMarketApiClient;
    private final IndexMarketRepository indexMarketRepository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * 홈 화면용 지수 조회
     * - DB에 저장된 최신 / 이전 지수 비교
     * - 외부 API 호출 없음
     */
    @Override
    public List<IndexMarketDto.Row> getLatestIndexes() {
        return List.of("코스피", "코스닥").stream()
                .map(this::buildRowFromDb)
                .filter(Objects::nonNull)
                .toList();
    }

    // 단일 지수 Row 생성 (DB 기준)
    private IndexMarketDto.Row buildRowFromDb(String indexName) {
        IndexMarket today = indexMarketRepository
                .findTopByIndexNameOrderByUpdatedAtDesc(indexName)
                .orElse(null);
        if (today == null) {
            log.warn("지수 데이터 없음 (today): {}", indexName);
            return null;
        }
        IndexMarket yesterday = indexMarketRepository
                .findFirstByIndexNameAndUpdatedAtLessThanOrderByUpdatedAtDesc(indexName,today.getUpdatedAt())
                .orElse(null);
        if (yesterday == null) {
            log.warn("지수 데이터 없음 (yesterday): {}", indexName);
            return null;
        }
        double rate = ((today.getClosePrice() - yesterday.getClosePrice()) / yesterday.getClosePrice()) * 100;

        return IndexMarketDto.Row.builder()
                .idxNm(indexName)
                .today(today.getClosePrice())
                .rate(rate)
                .updatedAt(today.getUpdatedAt())
                .build();
    }

    // 장 마감 이후 지수 API 호출
    @Override
    public void updateIndexes() {
        String baseDate = resolveBaseDate();
        for (String indexName : List.of("코스피", "코스닥")) {
            try {
                IndexMarketDto.ApiRow apiRow = indexMarketApiClient.fetchIndex(indexName, baseDate);
                if (apiRow == null) {
                    log.warn("지수 API 응답 없음: {}", indexName);
                    continue;
                }
                IndexMarket entity = IndexMarketDtoMapper.toEntity(apiRow);
                indexMarketRepository.save(entity);
            } catch (Exception e) {
                log.error("지수 저장 실패 ({}): {}", indexName, e.getMessage());
            }
        }
    }

    // 장 마감 기준 지수 조회 날짜 계산
    private String resolveBaseDate() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalDate baseDate = today;

        // 주말 보정
        if (today.getDayOfWeek() == DayOfWeek.SATURDAY) {
            baseDate = today.minusDays(1);
        } else if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            baseDate = today.minusDays(2);
        }

        // 장 마감 이전이면 전 거래일
        if (now.isBefore(LocalTime.of(15, 30))) {
            baseDate = previousWeekday(baseDate.minusDays(1));
        }
        return baseDate.format(FMT);
    }

    // 직전 평일 계산
    private LocalDate previousWeekday(LocalDate date) {
        LocalDate d = date;
        while (d.getDayOfWeek() == DayOfWeek.SATURDAY
                || d.getDayOfWeek() == DayOfWeek.SUNDAY) {
            d = d.minusDays(1);
        }
        return d;
    }
}