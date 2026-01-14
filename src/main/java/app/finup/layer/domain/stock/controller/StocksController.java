package app.finup.layer.domain.stock.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stock.enums.ChartType;
import app.finup.layer.domain.stock.service.StockAiService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 주식 종목정보 제공 REST API 클래스
 * @author kcw
 * @since 2026-01-13
 */

@Slf4j
@RestController
@RequestMapping(Url.STOCKS)
@RequiredArgsConstructor
public class StocksController {

    private final StockAiService stockAiService;

    /**
     * 종목 차트 분석 정보 제공 API
     * [GET] /stocks/{stockCode}/analysis/chart
     */
    @GetMapping("/{stockCode}/analysis/chart")
    public ResponseEntity<?> getChartAnalysis(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable String stockCode,
                                              @RequestParam ChartType chartType,
                                              @RequestParam(defaultValue = "false") boolean retry) {

        return retry ?
                Api.ok(stockAiService.retryAnalyzeChart(stockCode, userDetails.getMemberId(), chartType)) :
                Api.ok(stockAiService.analyzeChart(stockCode, userDetails.getMemberId(), chartType));
    }


    /**
     * 종목 상세 분석 정보 제공 API
     * [GET] /stocks/{stockCode}/analysis/detail
     */
    @GetMapping("/{stockCode}/analysis/detail")
    public ResponseEntity<?> getDetailAnalysis(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @PathVariable String stockCode,
                                               @RequestParam(defaultValue = "false") boolean retry) {

        return retry ?
                Api.ok(stockAiService.retryAnalyzeDetail(stockCode, userDetails.getMemberId())) :
                Api.ok(stockAiService.analyzeDetail(stockCode, userDetails.getMemberId()));
    }


    /**
     * 종목에 추천하는 유튜브 영상 목록 제공 API
     * [GET] /stocks/{stockCode}/recommendation/youtube
     */
    @GetMapping("/{stockCode}/recommendation/youtube")
    public ResponseEntity<?> getYouTubeRecommendation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @PathVariable String stockCode,
                                                      @RequestParam(defaultValue = "false") boolean retry) {

        return retry ?
                Api.ok(stockAiService.recommendYouTube(stockCode, userDetails.getMemberId())) :
                Api.ok(stockAiService.retryRecommendYouTube(stockCode, userDetails.getMemberId()));
    }


}
