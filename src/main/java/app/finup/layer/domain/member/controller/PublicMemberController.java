package app.finup.layer.domain.member.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping(Url.MEMBER_PUBLIC)
@RequiredArgsConstructor
public class PublicMemberController {

    private final MemberService memberService;


    /**
     * 회원가입
     * [POST] /public/api/members/join
     */

    @PostMapping
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto.Join rq) {
        MemberDto.Join rp = memberService.join(rq);
        return Api.ok(rp);
    }
}
