package app.finup.layer.domain.member.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 회원 컨트롤러 클래스
 * @author khj
 * @since 2025-12-04
 */

@Slf4j
@RestController
@RequestMapping(Url.MEMBER)
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    /**
     * 회원 리스트
     * [GET] api/members/list
     * @param rq 회원 목록 검색 요청 DTO
     */
    @GetMapping("/list")
    public ResponseEntity<?> getList(MemberDto.Search rq) {
        // [1] 요청
        Page<MemberDto.Row> rp = memberService.search(rq);

        // [2] 페이징 응답 전달
        return Api.ok(rp.getRows(), Pagination.of(rp));
    }

    /**
     * 회원 전체 리스트(PDF 전용)
     * [GET] api/members/list/all
     */

    @GetMapping("/list/all")
    public ResponseEntity<?> getAllMemberList() {
        // [1] 요청
        List<MemberDto.Row> rows = memberService.getMemberList();

        // [2] 페이징 응답 전달
        return Api.ok(rows);
    }
}
