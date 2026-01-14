package app.finup.layer.domain.indexMarket.service;

import app.finup.api.external.marketindex.client.MarketIndexClient;
import app.finup.layer.domain.indexMarket.dto.IndexMarketDto;
import app.finup.layer.domain.indexMarket.dto.IndexMarketDtoMapper;
import app.finup.layer.domain.indexMarket.entity.IndexMarket;
import app.finup.layer.domain.indexMarket.repository.IndexMarketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexMarketServiceImpl implements IndexMarketService {
    private final MarketIndexClient apiClient;
    private final IndexMarketRepository repository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.BASIC_ISO_DATE;

    // 홈 화면용 지수 조회
    @Override
    public List<IndexMarketDto.Row> getLatestIndexes() {
        return List.of("코스피", "코스닥").stream()
                .map(indexName ->
                        repository.findTopByIndexNameOrderByUpdatedAtDesc(indexName)
                                .map(IndexMarketDtoMapper::toRow)
                                .orElse(null)
                )
                .filter(Objects::nonNull)
                .toList();
    }

    // 지수 자동 갱신
    @Override
    public void updateIndexes() {
        for (String indexName : List.of("코스피", "코스닥")) {
            updateIndexIfNeeded(indexName);
        }
    }

    // 단일 지수 갱신
    private void updateIndexIfNeeded(String indexName) {
        // 오늘 날짜 기준으로 이미 갱신된 지수인지 확인
        boolean alreadyUpdated =
                repository.findTopByIndexNameOrderByUpdatedAtDesc(indexName)
                        .map(entity ->
                                entity.getUpdatedAt()
                                        .toLocalDate()
                                        .equals(LocalDate.now()))
                        .orElse(false);

        if (alreadyUpdated) {
            log.info("이미 갱신된 지수 → skip: {}", indexName);
            return;
        }

        // 최신 거래일 + 전 거래일 데이터 확보 (fallback 포함)
        IndexDataPair pair = fetchLatestPair(indexName);

        if (pair == null) {
            log.warn("지수 데이터 확보 실패: {}", indexName);
            return;
        }

        // 등락 계산
        double diff = pair.today - pair.yesterday;
        double rate = (diff / pair.yesterday) * 100;

        // DB 저장
        IndexMarket entity =
                repository.findTopByIndexNameOrderByUpdatedAtDesc(indexName)
                        .orElseGet(() ->
                                IndexMarket.builder()
                                        .indexName(indexName)
                                        .closePrice(0)
                                        .diff(0)
                                        .rate(0)
                                        .updatedAt(LocalDateTime.now())
                                        .build()
                        );
        entity.update(pair.today, diff, rate);
        repository.save(entity);

        log.info("지수 저장 완료: {}", indexName);
    }

    // 최신 거래일 / 전 거래일 지수 조회
    private IndexDataPair fetchLatestPair(String indexName) {
        // 기준일(오늘부터) 데이터 탐색
        LocalDate baseDate = LocalDate.now();
        IndexMarketDto.ApiRow todayRow = null;

        for (int i = 0; i < 7; i++) {
            todayRow = apiClient.fetchIndex(indexName, baseDate.format(FMT));
            if (todayRow != null) break;
            baseDate = baseDate.minusDays(1);
        }
        if (todayRow == null) return null;

        // 전 거래일 데이터 탐색
        LocalDate prevDate = baseDate.minusDays(1);
        IndexMarketDto.ApiRow prevRow = null;

        for (int i = 0; i < 7; i++) {
            prevRow = apiClient.fetchIndex(indexName, prevDate.format(FMT));
            if (prevRow != null) break;
            prevDate = prevDate.minusDays(1);
        }
        if (prevRow == null) return null;

        // 가격 파싱 후 반환
        return new IndexDataPair(
                parse(todayRow.getClpr()),
                parse(prevRow.getClpr())
        );
    }

    // 숫자 문자열 파싱 보조 메서드
    private double parse(String value) {
        return Double.parseDouble(value.replace(",", ""));
    }

    // 내부 전용 DTO
    private static class IndexDataPair {
        final double today;
        final double yesterday;

        IndexDataPair(double today, double yesterday) {
            this.today = today;
            this.yesterday = yesterday;
        }
    }
}