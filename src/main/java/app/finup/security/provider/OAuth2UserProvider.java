package app.finup.security.provider;

import app.finup.layer.domain.member.enums.MemberSocial;
import app.finup.security.dto.OAuth2UserInfo;

/**
 * OAuth2 Social Login 회원 로그인 기능 제공 인터페이스
 * @author u
 */

public interface OAuth2UserProvider {

    String getAccessToken(String code);

    OAuth2UserInfo getOAuth2UserInfo(String accessToken);

    MemberSocial getSocial();
}
