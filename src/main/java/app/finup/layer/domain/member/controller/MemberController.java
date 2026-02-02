package app.finup.layer.domain.member.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.member.service.MemberService;
import app.finup.security.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.Duration;
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

    @Value("${jwt.cookie-name}")
    private String jwtCookieName;

    @Value("${jwt.expiration.cookie}")
    private Duration jwtCookieExpiration;


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

    /**
     *현재 로그인한 회원 정보 조회
     * [GET] /api/members/me/detail
     * @return 로그인한 회원 정보
     */
    @GetMapping("/me/detail")
    public ResponseEntity<?> getDetail(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        return Api.ok(memberService.getDetail(memberId));
    }

    /**
    * 회원가입
    * [POST] /api/members
    */
    @PostMapping
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto.Join rq) {

        log.info("[JOIN][REQUEST] email={}", rq.getEmail());

        MemberDto.Row rp = memberService.join(rq);

        log.info("[JOIN][SUCCESS] memberId={}, email={}",
                rp.getMemberId(), rp.getEmail());

        return Api.ok(rp);
    }

  
    /**
     * 회원 닉네임 수정 API
     * [PATCH] /members/me/nickname
     * @param userDetails 회원 정보
     * @param rq 닉네임 수정 요청 DTO
     */
    @PatchMapping("/me/nickname")
    public ResponseEntity<?> editNickname(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody MemberDto.EditNickname rq) {

        rq.setMemberId(userDetails.getMemberId());
        return Api.ok(AppStatus.MEMBER_OK_EDIT_NICKNAME, memberService.editNickname(rq));
    }
    /**
     * 회원 비밀번호 수정 API
     * [PATCH] /members/me/password
     * @param userDetails 회원 정보
     * @param rq 비밀번호 수정 요청 DTO
     */
    @PatchMapping("/me/password")
    public ResponseEntity<?> editPassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody MemberDto.EditPassword rq) {
        rq.setMemberId(userDetails.getMemberId());

        // [1] 수정 요청
        memberService.editPassword(rq);

        // [2] 성공 응답
        return Api.ok(AppStatus.MEMBER_OK_EDIT_PASSWORD);
    }
    /**
     * 회원 프로필 이미지 수정 API
     * [POST] /members/me/profile-image
     * @param userDetails 유저 정보
     * @param file 업로드 이미지 파일
     */
    @PostMapping(
            value = "/me/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> editProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestParam("file") MultipartFile file) {

        return Api.ok(
                AppStatus.MEMBER_OK_EDIT_PROFILE,
                memberService.editProfileImage(userDetails.getMemberId(), file)
        );
    }

}
