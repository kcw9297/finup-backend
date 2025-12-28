package app.finup.layer.domain.memberWordbook.controller;

import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.memberWordbook.dto.MemberWordbookDto;
import app.finup.layer.domain.memberWordbook.service.MemberWordbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 회원 단어장 REST API
 * 인증된 회원만 접근 가능
 *
 * @author khj
 * @since 2025-12-14
 */


@RestController
@RequiredArgsConstructor
@RequestMapping(Url.MEMBER_WORDBOOK)
public class MemberWordbookController {

    private final MemberWordbookService memberWordbookService;

    /**
     * 내 단어장 목록 조회
     * [GET] /api/members/wordbook
     */
    @GetMapping
    public ResponseEntity<?> getMyWordbook() {
        return Api.ok(memberWordbookService.getMyWordbook());
    }

    /**
     * 단어장에 단어 추가
     * [POST] /api/members/wordbook
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody MemberWordbookDto.Add rq) {
        memberWordbookService.add(rq);
        return Api.ok(rq);
    }

    /**
     * 단어장에서 단어 삭제
     * [DELETE] /api/members/wordbook/{termId}
     */
    @DeleteMapping("/{termId:[0-9]+}")
    public ResponseEntity<?> remove(@PathVariable Long termId) {
        memberWordbookService.remove(termId);
        return Api.ok();
    }

    /**
     * 특정 단어가 내 단어장에 있는지 여부 조회
     * [GET] /api/members/wordbook/{termId}
     */
    @GetMapping("/{termId:[0-9]+}")
    public ResponseEntity<?> isAdded(@PathVariable Long termId) {
        return Api.ok(memberWordbookService.isAdded(termId));
    }


    /**
     * 단어장 암기 상태 변경 API
     * - 단어를 암기 완료 또는 암기 취소 상태로 변경한다.
     * - 로그인한 사용자 본인의 단어장에 대해서만 처리된다.
     *
     * [PATCH] /wordbooks/{termId}/memorize
     */

    @PatchMapping("/{termId:[0-9]+}/memorize")
    public ResponseEntity<?> memorize(
            @PathVariable Long termId,
            @RequestBody MemberWordbookDto.Memorize rq
    ) {
        return Api.ok(
                AppStatus.WORD_MEMORIZE_OK
                , memberWordbookService.memorize(termId, rq)
        );
    }
}
