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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
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

    @PostMapping
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto.Join rq) {
        MemberDto.Join rp = memberService.join(rq);
        return Api.ok(rp);
    }
    /**
     * 회원 닉네임 수정 API
     * [PATCH] /members/{memberId}/nickname
     * @param memberId 회원 번호
     * @param rq 닉네임 수정 요청 DTO
     */
    @PatchMapping("/{memberId:[0-9]+}/nickname")
    public ResponseEntity<?> editNickname(@PathVariable Long memberId,
                                          @RequestBody MemberDto.EditNickname rq) {

        // [1] 수정 요청
        memberService.editNickname(memberId, rq);

        // [2] 성공 응답
        return Api.ok();
    }
    /**
     * 회원 비밀번호 수정 API
     * [PATCH] /members/{memberId}/password
     * @param memberId 회원 번호
     * @param rq 비밀번호 수정 요청 DTO
     */
    @PatchMapping("/{memberId:[0-9]+}/password")
    public ResponseEntity<?> editPassword(@PathVariable Long memberId,
                                          @RequestBody MemberDto.EditPassword rq) {

        // [1] 수정 요청
        memberService.editPassword(memberId, rq);

        // [2] 성공 응답
        return Api.ok();
    }
    /**
     * 회원 프로필 이미지 수정 API
     * [PATCH] /members/{memberId}/profile-image
     * @param memberId 회원 번호
     * @param file 업로드 이미지 파일
     */
    @PatchMapping("/{memberId:[0-9]+}/profile-image")
    public ResponseEntity<?> editProfileImage(@PathVariable Long memberId,
                                              @RequestParam("file") MultipartFile file) {

        memberService.editProfileImage(memberId, file);
        return Api.ok();
    }

}
