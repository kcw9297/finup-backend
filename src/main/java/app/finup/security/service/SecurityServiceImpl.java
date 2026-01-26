package app.finup.security.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.security.constant.SecurityRedisKey;
import app.finup.security.dto.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * SecurityService 구현 클래스
 * @author kcw
 * @since 2026-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final MemberRepository memberRepository;
    private final FileStorage fileStorage;


    @Cacheable(
            value = SecurityRedisKey.CACHE_LOGIN_MEMBER,
            key = "#memberId"
    )
    @Override
    public LoginMember getLoginMember(Long memberId) {
        return memberRepository
                .findByIdWithProfileImage(memberId)
                .map(this::toLoginMember)
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));
    }


    // LoginMember DTO 변환
    private LoginMember toLoginMember(Member member) {

        return LoginMember.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole().name())
                .social(member.getSocial().name())
                .profileImageUrl(
                        Objects.isNull(member.getProfileImageFile()) || Objects.isNull(member.getProfileImageFile().getFilePath())?
                                null : fileStorage.getUrl(member.getProfileImageFile().getFilePath())
                )
                .build();
    }

}
