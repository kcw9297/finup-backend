package app.finup.layer.domain.member.enums;

import lombok.Getter;

@Getter
public enum MemberSocial {

    NORMAL("일반로그인"),
    NAVER("네이버"),
    KAKAO("카카오"),
    LINE("라인"),
    GOOGLE("구글");

    private final String value;

    MemberSocial(String value) {
        this.value = value;
    }
}
