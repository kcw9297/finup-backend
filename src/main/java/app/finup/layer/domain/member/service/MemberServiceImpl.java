package app.finup.layer.domain.member.service;

import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.base.template.UploadFileCodeTemplate;
import app.finup.layer.domain.auth.redis.AuthRedisStorage;
import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.member.dto.MemberDtoMapper;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.mapper.MemberMapper;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.security.constant.SecurityRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;


import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor


public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthRedisStorage authRedisStorage;
    private final FileStorage fileStorage;


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



    @CacheEvict(
            value = SecurityRedisKey.CACHE_LOGIN_MEMBER,
            key = "#rq.memberId"
    )
    @Override
    public String editNickname(MemberDto.EditNickname rq) {

        // [1] 회원 조회
        Member member = getMember(rq.getMemberId());

        // [2] 닉네임 중복 체크
        if (memberRepository.existsByNickname(rq.getNickname()))
            throw new BusinessException(AppStatus.MEMBER_DUPLICATE_NICKNAME);

        // [3] 닉네임 수정
        member.editNickname(rq.getNickname());
        return member.getNickname();
    }


    @Override
    public void editPassword(MemberDto.EditPassword rq) {

        // [1] 회원 조회
        Member member = getMember(rq.getMemberId());

        // [2] 현재 비밀번호 검증 (수정 전/후와 같은지 확인)
        if (passwordEncoder.matches(rq.getNewPassword(), member.getPassword()))
            throw new BusinessException(AppStatus.MEMBER_EQUAL_PASSWORD);

        // [3] 새 비밀번호 암호화 후 변경
        member.editPassword(passwordEncoder.encode(rq.getNewPassword()));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));
    }




    @CacheEvict(
            value = SecurityRedisKey.CACHE_LOGIN_MEMBER,
            key = "#memberId"
    )
    @Override
    public String editProfileImage(Long memberId, MultipartFile file) {

        // [1] 멤버 데이터 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));
        
        // [2] 변경 전 원래 이미지 정보 추출 후, 소유자를 null로 변경
        // Soft Delete 처리 (나중에 스케줄러에서 파일 삭제)
        if (Objects.nonNull(member.getProfileImageFile())) member.removeProfileImage().softRemove();

        // [3] 프로필 이미지 업로드 처리 후 프로필 이미지 URL 반환
        return UploadFileCodeTemplate.uploadFileAndSaveEntity(
                fileStorage,
                file, memberId, FileOwner.MEMBER, FileType.PROFILE,
                member::editProfileImage
        );

    }


    @Override
    @Transactional(readOnly = true)
    public MemberDto.Detail getDetail(Long memberId) {

        // [1] 회원 조회 (존재 여부 검증 포함)
        Member member = memberRepository.findByIdWithProfileImage(memberId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));

        // [2] Entity → DTO 변환
        MemberDto.Detail memberDetail = MemberDtoMapper.toDetail(member);

        // [3] 프로필 이미지 URL 보정
        UploadFile profileImageFile = member.getProfileImageFile();
        if (Objects.nonNull(profileImageFile) && Objects.nonNull(profileImageFile.getFilePath()))
            memberDetail.setProfileImageUrl(fileStorage.getUrl(profileImageFile.getFilePath()));

        return memberDetail;
    }

}

