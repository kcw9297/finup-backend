package app.finup.security.service;

import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.enums.MemberSocial;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Objects;

/**
 * 일반회원 로그인을 처리할 UserDetails Service 커스텀 구현체
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@RequiredArgsConstructor
public class NormalUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final FileStorage fileStorage;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // [1] 회원 정보 조회
        Member member = memberRepository
                .findByEmailAndSocial(username, MemberSocial.NORMAL)
                .orElseThrow(() -> new UsernameNotFoundException("")); // 오류 메세지는 외부에서 처리

        // [2] UserDetails 생성 및 반환
        UploadFile profileImage = member.getProfileImageFile();

        return CustomUserDetails.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .isActive(member.getIsActive())
                .role(member.getRole().name())
                .social(member.getSocial().name())
                .profileImageUrl(
                        Objects.isNull(profileImage) ? null : fileStorage.getUrl(profileImage.getFilePath())
                )
                .authorities(List.of(new SimpleGrantedAuthority(member.getRole().getAuthority())))
                .build();
    }

}
