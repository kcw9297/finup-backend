package app.finup.layer.domain.member.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.auth.redis.AuthRedisStorage;
import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.member.dto.MemberDtoMapper;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.mapper.MemberMapper;
import app.finup.layer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.service.UploadFileService;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor


public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    private final PasswordEncoder passwordEncoder;
    private final AuthRedisStorage authRedisStorage;

    private final UploadFileService uploadFileService;


    @Override
    @Transactional(readOnly = true)
    public Page<MemberDto.Row> search(MemberDto.Search rq) {

        List<MemberDto.Row> rp = memberMapper.search(rq);
        Long count = memberMapper.countForSearch(rq);

        return Page.of(rp, count.intValue(), rq.getPageNum(), rq.getPageSize());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDto.Row> getMemberList() {
        return memberRepository.findAll().stream()
                .map(MemberDtoMapper::toRow)
                .toList();
    }

    //  회원가입
    @Override
    @Transactional
    public MemberDto.Join join(MemberDto.Join rq) {

        // 1) 이메일 중복 체크
        if (memberRepository.existsByEmail(rq.getEmail())) {
            throw new BusinessException(AppStatus.AUTH_DUPLICATE_EMAIL);
        }

        // 2) 이메일 인증 완료 여부 확인 (Redis VERIFIED 키)
        if (!authRedisStorage.isVerified(rq.getEmail())) {
            // 지금은 임시로 AUTH_INVALID_REQUEST 쓰고 있지만,
            // 실무에선 "AUTH_JOIN_EMAIL_NOT_VERIFIED" 같은 status를 따로 두는게 좋음
            throw new BusinessException(AppStatus.AUTH_INVALID_REQUEST);
        }

        // 3) 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rq.getPassword());

        // 4) 닉네임 생성 (임시) - 나중에 입력 받거나 정책으로 바꾸면 됨
        String nickname = "user_" + System.currentTimeMillis();

        // 5) 엔티티 생성
        Member member = Member.joinNormal(
                rq.getEmail(),
                encodedPassword,
                nickname
        );

        // 6) 저장
        log.info("[MEMBER] join request email={}", rq.getEmail()); // join 시작

        Member newMember = memberRepository.save(member);
        log.info("[MEMBER] saved memberId={}, email={}", newMember.getMemberId(), newMember.getEmail()); // save 직후

        log.info("[MEMBER] join done memberId={}", newMember.getMemberId()); // 끝


        MemberDto.Join newMemberJoinDto = MemberDtoMapper.toMemberJoinDto(newMember);

        // 7) 인증 완료 마크 제거 (재사용 방지)
        try {
            authRedisStorage.removeVerified(rq.getEmail());
        } catch (Exception e) {
            log.warn("[MEMBER] Redis cleanup failed after join. email={}", rq.getEmail());
        }

        return newMemberJoinDto;
    }

    /**
     * 조회를 위해 사용
     * @param memberId
     * @return
     */
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));
    }
    /**
     * 닉네임 수정
     * @param memberId 회원 번호
     * @param rq 닉네임 수정 요청 DTO
     */
    @Override
    public void editNickname(Long memberId, MemberDto.EditNickname rq) {

        // [1] 회원 조회
        Member member = getMember(memberId);

        // [2] 닉네임 중복 체크 (본인 제외)
        if (memberRepository.existsByNicknameAndMemberIdNot(rq.getNickname(), memberId)) {
            throw new BusinessException(AppStatus.MEMBER_DUPLICATE_NICKNAME);
        }

        // [3] 닉네임 수정
        member.changeNickname(rq.getNickname());

    }
    /**
     * 비밀번호 수정
     * @param memberId 회원 번호
     * @param rq 비밀번호 수정 요청 DTO
     */
    @Override
    public void editPassword(Long memberId, MemberDto.EditPassword rq) {

        // [1] 회원 조회
        Member member = getMember(memberId);

        // [2] 현재 비밀번호 검증
        if (!passwordEncoder.matches(rq.getCurrentPassword(), member.getPassword())) {
            throw new BusinessException(AppStatus.AUTH_BAD_CREDENTIALS);
        }

        // [3] 새 비밀번호 암호화 후 변경
        member.changePassword(passwordEncoder.encode(rq.getNewPassword()));
    }
    /**
     * 프로필 이미지 수정
     * @param memberId 회원 번호
     * @param file 업로드 이미지 파일
     */
    @Override
    public void editProfileImage(Long memberId, MultipartFile file) {

        // [1] 회원 조회
        Member member = getMember(memberId);

        // [2] 파일 업로드 (UploadFile 생성)
        UploadFile uploadFile = uploadFileService.upload(file);

        // [3] 프로필 이미지 변경
        member.changeProfileImage(uploadFile);
    }
}