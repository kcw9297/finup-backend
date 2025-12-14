package app.finup.layer.domain.memberWordbook.controller;

import app.finup.common.constant.Url;
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
     * [DELETE] /api/members/wordbook
     */
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody MemberWordbookDto.Remove rq) {
        memberWordbookService.remove(rq);
        return Api.ok();
    }
}
