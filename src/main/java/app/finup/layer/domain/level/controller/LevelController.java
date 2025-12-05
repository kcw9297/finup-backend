package app.finup.layer.domain.level.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.level.dto.LevelDto;
import app.finup.layer.domain.level.service.LevelService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 개념 학습 단계 Controller
 * - 사용자: 단계 리스트/상세 조회
 * - 관리자: 등록/수정/삭제
 * - 회원: 진행률 업데이트
 *
 * @author sjs
 * @since 2025-12-05
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(Url.LEVEL)
public class LevelController {

    private final LevelService levelService;

    /**
     * 전체 단계 리스트 조회
     * [GET] /levels
     */
    @GetMapping
    public ResponseEntity<?> getList() {

        Long memberId = null;

        return Api.ok(levelService.getList(memberId));
    }

    /**
     * 단일 단계 상세 조회
     * [GET] /levels/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return Api.ok(levelService.getDetail(id));
    }

    /**
     * 관리자: 단계 등록
     * [POST] /levels
     */
    @PostMapping
    public ResponseEntity<?> write(@RequestBody LevelDto.Write dto) {
        Long newId = levelService.write(dto);
        return Api.ok(newId);
    }

    /**
     * 관리자: 단계 수정
     * [PUT] /levels
     */
    @PutMapping
    public ResponseEntity<?> edit(@RequestBody LevelDto.Edit dto) {
        levelService.edit(dto);
        return Api.ok();
    }

    /**
     * 관리자: 단계 삭제
     * [DELETE] /levels/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        levelService.delete(id);
        return Api.ok();
    }

    /**
     * 회원: 단계 진행률 업데이트
     * [POST] /levels/{id}/progress
     * 요청 JSON: { "progress": 40 }
     */
    @PostMapping("/{id}/progress")
    public ResponseEntity<?> updateProgress(
            @PathVariable Long id,
            @RequestBody ProgressRequest request
    ) {


        Long memberId = 1L; // 임시

        levelService.updateProgress(memberId, id, request.getProgress());

        return Api.ok();
    }

    /**
     * 진행률 업데이트 요청 DTO
     */
    @Data
    public static class ProgressRequest {
        private Integer progress;
    }
}
