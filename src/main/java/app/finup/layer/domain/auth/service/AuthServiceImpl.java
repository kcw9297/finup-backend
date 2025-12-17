package app.finup.layer.domain.auth.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.infra.mail.MailProvider;
import app.finup.layer.domain.auth.redis.AuthRedisStorage;
import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.member.dto.MemberDtoMapper;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final MailProvider mailProvider;
    private final AuthRedisStorage authRedisStorage;

    // 회원가입 이메일 인증 코드 유효시간 (5분)
    private static final Duration JOIN_EMAIL_EXPIRATION = Duration.ofMinutes(5);

    // 인증 완료 상태 유효시간 (10분)
    private static final Duration JOIN_EMAIL_VERIFIED_TTL = Duration.ofMinutes(10);


    /**
     * 회원가입용 이메일 인증 코드 발송
     */
    @Override
    @Transactional
    public void sendJoinEmail(String email) {

        // 1. 이미 가입된 이메일인지 검증
        if (memberRepository.existsByEmail(email)) {   // 없으면 Repository에 메서드 추가 필요
            throw new BusinessException(AppStatus.AUTH_DUPLICATE_EMAIL);
        }

        // 2. 6자리 인증코드 생성
        String code = createVerificationCode();

        // 3. Redis에 인증코드 저장 (TTL 적용)
        authRedisStorage.saveEmailCode(email, code, JOIN_EMAIL_EXPIRATION);

        // 4. 이메일 발송
        mailProvider.sendVerifyCode(email, code, JOIN_EMAIL_EXPIRATION);

        log.info("[AUTH] 회원가입 이메일 인증코드 발송 완료. email={}, code={}", email, code);
    }

    /**
     * 회원가입용 이메일 인증 코드 검증
     */
    @Override
    @Transactional
    public void verifyJoinEmail(String email, String code) {

        // [1] Redis에서 저장된 코드 조회
        String savedCode = authRedisStorage.getEmailCode(email);

        // [1-1] 저장된 코드가 없으면 (TTL 만료 or 요청 없음)
        if (savedCode == null) {
            throw new BusinessException(AppStatus.AUTH_INVALID_REQUEST);
        }

        // [1-2] 코드 일치 여부 확인
        if (!savedCode.equals(code)) {
            throw new BusinessException(AppStatus.AUTH_INVALID_REQUEST);
        }

        // [2] 성공 시 Redis에서 제거 (한 번만 사용)
        authRedisStorage.removeEmailCode(email);

        // [3] 인증완료 마크 저장 (회원가입에서 검사할 근거)
        authRedisStorage.markVerified(email, Duration.ofMinutes(10));

        log.info("[AUTH] 회원가입 이메일 인증 성공. email={}", email);
    }

    @Override
    public MemberDto.Detail getProfile(Long memberId) {
        Member member = memberRepository.findByIdWithProfileImage(memberId)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));

        return MemberDtoMapper.toDetail(member);
    }

    // 내부 유틸: 6자리 난수 생성 (000000 ~ 999999)
    private String createVerificationCode() {
        int value = (int) (Math.random() * 1_000_000);
        return String.format("%06d", value);
    }
}
