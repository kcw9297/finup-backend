package app.finup.security.service;

import app.finup.security.dto.LoginMember;

/**
 * Spring Security 관련 비즈니스 로직 제공 인터페이스
 * @author kcw
 * @since 2026-01-26
 */
public interface SecurityService {

    /**
     * 로그인 회원 정보 조회 (캐싱 데이터)
     * @param memberId 대상 회원 번호 (인증 대상)
     * @return 회원 정보 DTO
     */
    LoginMember getLoginMember(Long memberId);

}
