package app.finup.layer.domain.videolink.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.videolink.service.VideoLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private final VideoLinkService videoLinkService;

    /**
     * 학습 영상 검색
     * [GET /video-links/search
     * @param rq 영상 검색요청 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(VideoLinkDto.Search rq) {

        // [1] 학습 영상 수정
        Page<VideoLinkDto.Row> rp = videoLinkService.getPagedList(rq);

        // [2] 성공 응답 반환
        return Api.ok(rp.getRows(), Pagination.of(rp));
    }


    /**
     * 학습 영상 추가
     * [POST] /video-links
     * @param rq 영상 추가요청 DTO
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody VideoLinkDto.Add rq) {

        // [1] 학습 영상 수정
        videoLinkService.add(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.VIDEO_LINK_OK_ADD);
    }


    /**
     * 학습 영상 추가
     * [PUT] /video-links/{videoLinkId}
     * @param rq 영상 갱신요청 DTO
     */
    @PutMapping("/{videoLinkId:[0-9]+}")
    public ResponseEntity<?> edit(@PathVariable Long videoLinkId,
                                  @RequestBody VideoLinkDto.Edit rq) {

        // [1] 학습 영상 수정
        rq.setVideoLinkId(videoLinkId);
        VideoLinkDto.Row rp = videoLinkService.edit(rq);

        // [2] 성공 응답 반환 (갱신 결과 전달)
        return Api.ok(AppStatus.VIDEO_LINK_OK_EDIT, rp);
    }


    /**
     * 학습 영상 삭제
     * [DELETE] /video-links/{videoLinkId}
     */
    @DeleteMapping("/{videoLinkId:[0-9]+}")
    public ResponseEntity<?> remove(@PathVariable Long videoLinkId) {

        // [1] 학습 영상 삭제
        videoLinkService.remove(videoLinkId);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.VIDEO_LINK_OK_REMOVE);
    }
}
