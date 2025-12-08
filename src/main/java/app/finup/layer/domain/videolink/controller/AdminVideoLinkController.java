package app.finup.layer.domain.videolink.controller;

import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.videolink.enums.VideoLinkOwner;
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
     * 현재 자원에 속하는 영상 목록 조회
     * [GET] /video-links
     * @param ownerId 영상 소유자 번호
     * @param videoLinkOwner 영상 소유자 (HOME, STUDY, ...)
     */
    @GetMapping
    public ResponseEntity<?> getList(@RequestParam Long ownerId,
                                     @RequestParam VideoLinkOwner videoLinkOwner) {

        return Api.ok(videoLinkService.getList(ownerId, videoLinkOwner));
    }
    /**
     * 학습 영상 추가
     * [POST] /video-links
     * @param rq 영상 추가요청 DTO
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody VideoLinkDto.Add rq) {
        log.info("ADD-REQUEST videoUrl={}, ownerId={}, owner={}",
                rq.getVideoUrl(), rq.getOwnerId(), rq.getVideoLinkOwner());
        // [1] 학습 영상 추가
        videoLinkService.add(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.VIDEO_LINK_OK_ADD);
    }


    /**
     * 학습 영상 위치 재정렬
     * [PATCH] /video-links/{videoLinkId}/reorder
     * @param rq 영상 재정렬 요청 DTO
     */
    @PatchMapping("/{videoLinkId:[0-9]+}/reorder")
    public ResponseEntity<?> reorder(@PathVariable Long videoLinkId,
                                     @RequestBody VideoLinkDto.Reorder rq) {

        // [1] 재정렬 수행
        rq.setVideoLinkId(videoLinkId);
        videoLinkService.reorder(rq);

        // [2] 성공 응답 반환
        return Api.ok();
    }


    /**
     * 학습 영상 추가
     * [PUT] /video-links/{videoLinkId}
     * @param rq 영상 추가요청 DTO
     */
    @PutMapping("/{videoLinkId:[0-9]+}")
    public ResponseEntity<?> edit(@PathVariable Long videoLinkId,
                                  @RequestBody VideoLinkDto.Edit rq) {

        // [1] 학습 영상 수정
        rq.setVideoLinkId(videoLinkId);
        videoLinkService.edit(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.VIDEO_LINK_OK_EDIT);
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
