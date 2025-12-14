package app.finup.layer.domain.indexMarket.controller;

import app.finup.common.constant.Url;
import app.finup.layer.domain.indexMarket.dto.IndexMarketDto;
import app.finup.layer.domain.indexMarket.service.IndexMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 홈페이지 지수 API 클래스
 * @author phj
 * @since 2025-12-11
 */

@Slf4j
@RequestMapping(Url.HOME_MARKET_PUBLIC)
@RestController
@RequiredArgsConstructor
public class IndexMarketController {
    private final IndexMarketService indexMarketService;

    // 지수 조회
    @GetMapping("/latest")
    public List<IndexMarketDto.Row> getLatestIndexes() {
        return indexMarketService.getLatestIndexes();
    }
}
