package app.finup.layer.domain.exchangeRate.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.exchangeRate.dto.ExchangeRateDto;
import app.finup.layer.domain.exchangeRate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 홈페이지 환율 API 클래스
 * @author phj
 * @since 2025-12-11
 */

@Slf4j
@RequestMapping(Url.HOME_EXCHANGE_PUBLIC)
@RestController
@RequiredArgsConstructor
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    /**
     * 최신 환율 조회 API
     * - 평일 11시 이후 → 당일 환율
     * - 평일 11시 이전 → 전일 환율
     * - 주말 / 연휴 → 마지막 영업일 환율
     * - fallback 최대 10일
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestRates() {
        List<ExchangeRateDto.Row> rp = exchangeRateService.getLatestRates();
        return Api.ok(rp);
    }

    // 특정 통화 환율 조회 API
    @GetMapping("/{currency:[A-Z]{3}}")
    public ResponseEntity<?> getRate(@PathVariable String currency) {
        ExchangeRateDto.Row rp = exchangeRateService.getRate(currency);
        return Api.ok(rp);
    }

    // DB 캐시된 환율 전체 조회 API
    @GetMapping("/all")
    public ResponseEntity<?> getAllRates() {
        List<ExchangeRateDto.Row> rp = exchangeRateService.getAllRates();
        return Api.ok(rp);
    }
}