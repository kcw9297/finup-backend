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
import app.finup.security.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import app.finup.layer.domain.uploadfile.manager.UploadFileManager;


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

    private final UploadFileManager uploadFileManager;


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

    /**
     * 회원가입
     */
    @Override
    @Transactional
    public MemberDto.Row join(MemberDto.Join rq) {

        // 1) 이메일 중복 체크
        if (memberRepository.existsByEmail(rq.getEmail())) {
            throw new BusinessException(AppStatus.AUTH_DUPLICATE_EMAIL);
        }

        // 2) 이메일 인증 완료 여부 확인 (Redis VERIFIED 키)
        if (!authRedisStorage.isVerified(rq.getEmail())) {
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

        log.info("[JOIN][SERVICE][REQUEST] email={}", rq.getEmail());

        Member saved = memberRepository.save(member);

        log.info("[JOIN][SERVICE][SAVED] memberId={}, email={}",
                saved.getMemberId(), saved.getEmail());

        try {
            authRedisStorage.removeVerified(rq.getEmail());
        } catch (Exception e) {
            log.warn("[JOIN][SERVICE] removeVerified failed email={}", rq.getEmail(), e);
        }

        return MemberDtoMapper.toRow(saved);
    }


    /**
     * 조회를 위해 사용
     *
     * @param memberId
     * @return
     */
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));
    }

    /**
     * 닉네임 수정
     *
     * @param
     * @param rq 닉네임 수정 요청 DTO
     */
    @Override
    public void editNickname(MemberDto.EditNickname rq) {

        Long memberId = rq.getMemberId();

        // [1] 회원 조회
        Member member = getMember(memberId);

        // [2] 닉네임 중복 체크 (본인 제외)
        if (memberRepository.existsByNicknameAndMemberIdNot(rq.getNickname(), memberId)) {
            throw new BusinessException(AppStatus.MEMBER_DUPLICATE_NICKNAME);
        }

        // [3] 닉네임 수정
        member.editNickname(rq.getNickname());

    }

    /**
     * 비밀번호 수정
     *
     * @param
     * @param rq 비밀번호 수정 요청 DTO
     */
    @Override
    public void editPassword(MemberDto.EditPassword rq) {

        Long memberId = rq.getMemberId();

        // [1] 회원 조회
        Member member = getMember(memberId);

        // [2] 현재 비밀번호 검증
        if (!passwordEncoder.matches(rq.getCurrentPassword(), member.getPassword())) {
            throw new BusinessException(AppStatus.AUTH_BAD_CREDENTIALS);
        }

        // [3] 새 비밀번호 암호화 후 변경
        member.editPassword(passwordEncoder.encode(rq.getNewPassword()));
    }

    /**
     * 프로필 이미지 수정
     *
     * @param memberId 회원 번호
     * @param file     업로드 이미지 파일
     */
    @Override
    public void editProfileImage(Long memberId, MultipartFile file) {

        log.info("[PROFILE_IMAGE][SERVICE][START] memberId={}", memberId);

        if (file == null || file.isEmpty()) {
            throw new BusinessException(AppStatus.VALIDATION_INVALID_PARAMETER);
        }

        Member member = getMember(memberId);

        // 1) 기존 이미지 soft delete + 연관 끊기
        if (member.getProfileImageFile() != null) {
            member.removeProfileImage().softRemove();
        }

        // 2) UploadFile 엔티티 생성
        UploadFile newFile = uploadFileManager.setEntity(
                file,
                memberId,
                FileOwner.MEMBER,
                FileType.UPLOAD
        );

        // 3) 연관관계 연결
        member.editProfileImage(newFile); // 또는 member.editProfileImage(newFile)

        // 4) 실제 파일 저장
        uploadFileManager.store(file, newFile.getFilePath());

        log.info("[PROFILE_IMAGE][SERVICE][DONE] memberId={}, uploadFileId={}",
                memberId, newFile.getUploadFileId());
    }


    @Override
    @Transactional(readOnly = true)
    public MemberDto.Row getDetail(Long memberId) {
        // [1] 회원 조회 (존재 여부 검증 포함)
        Member member = getMember(memberId);

        // [2] Entity → DTO 변환
        MemberDto.Row row = MemberDtoMapper.toRow(member);

        // [3] 프로필 이미지 URL 보정
        if (member.getProfileImageFile() != null) {
            row.setProfileImageUrl(
                    uploadFileManager.getFullUrl(
                            member.getProfileImageFile().getFilePath()
                    )
            );
        }

        return row;
    }
}