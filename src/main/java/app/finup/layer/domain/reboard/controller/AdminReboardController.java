package app.finup.layer.domain.reboard.controller;

import app.finup.common.constant.Url;
import app.finup.layer.domain.reboard.service.ReboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 게시글 관리자용 REST API 클래스
 * @author kcw
 * @since 2025-11-24
 */

@Slf4j
@RestController
@RequestMapping(Url.REBOARD_ADMIN)
@RequiredArgsConstructor
public class AdminReboardController {

    private final ReboardService reboardService;


}