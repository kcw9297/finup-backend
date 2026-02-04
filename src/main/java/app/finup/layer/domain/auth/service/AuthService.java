package app.finup.layer.domain.auth.service;

public interface AuthService {

    /**
     * 회원가입용 이메일 인증코드 발송
     */
    void sendJoinEmail(String email);


    /**
     * 회원가입용 이메일 인증코드 검증
     */
    void verifyJoinEmail(String email, String code);


}
