package app.finup.layer.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberJoinDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class JoinNember {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])[^\s]{8,20}$",
                message = "비밀번호는 영문/숫자/특수문자를 포함한 8~20자여야 합니다."
        )
        private String password;
    }
}
