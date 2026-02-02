package app.finup.layer.domain.indicator.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.indicator.service.IndicatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 홈페이지 환율 API 클래스
 * @author phj
 * @since 2025-12-11
 */

@Slf4j
@RequestMapping(Url.INDICATOR_PUBLIC)
@RestController
@RequiredArgsConstructor
public class PublicIndicatorController {
    private final IndicatorService indicatorService;

    /**
     * 금융 지표 조회
     * [GET] /indicators/index/financial
     */
    @GetMapping("/index/financial")
    public ResponseEntity<?> getFinancialIndexes() {
        return Api.ok(indicatorService.getFinancialIndexes());
    }


    /**
     * 주식 시장 지표 조회
     * [GET] /indicators/index/market
     */
    @GetMapping("/index/market")
    public ResponseEntity<?> getMarketIndexes() {
        return Api.ok(indicatorService.getMarketIndexes());
    }

}