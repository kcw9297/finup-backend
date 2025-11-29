package app.finup.layer.domain.member.enums;

import lombok.Getter;

@Getter
public enum MemberRole {

    NORMAL("일반회원"),
    ADMIN("관리자");

    private final String value;

    MemberRole(String value) {
        this.value = value;
    }

    public String getAuthority() {
        return "ROLE_%s".formatted(this.name());
    }
}
