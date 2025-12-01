package app.finup.security.service;

import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 일반회원 로그인을 처리할 UserDetails Service 커스텀 구현체
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NormalUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // [1] 회원 로그인
        Member member = memberRepository
                .findByEmailWithProfileImage(username)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        // [2] UserDetails 객체 생성 및 반환
        UploadFile profileImage = member.getProfileImage();

        return CustomUserDetails.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .isActive(member.getIsActive())
                .role(member.getRole().name())
                .social(member.getSocial().name())
                .profileImageUrl(Objects.isNull(profileImage) ? null : profileImage.getFilePath())
                .authorities(List.of(new SimpleGrantedAuthority(member.getRole().getAuthority())))
                .build();
    }
}
