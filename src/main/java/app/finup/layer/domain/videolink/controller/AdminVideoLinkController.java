package app.finup.layer.domain.videolink.controller;

import app.finup.common.constant.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 학습용 영상 정보 공개용 REST API 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@RestController
@RequestMapping(Url.VIDEO_LINK_ADMIN)
@RequiredArgsConstructor
public class AdminVideoLinkController {
}
