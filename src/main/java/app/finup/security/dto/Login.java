package app.finup.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 시큐리티 일반 로그인 처리 DTO
 * @author kcw
 * @since 2025-11-26
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Login {

    public String email;
    public String password;
}
