package app.finup.security.handler;

import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.common.utils.LogUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // [1] 오류 로그 출력
        LogUtils.showWarn(this.getClass(), "권한 부족으로 인한 요청 실패. [%s] - [%s]",
                request.getRequestURI(),
                accessDeniedException.getMessage()
        );

        // [2] 권한 부족 응답 반환
        Api.writeFail(response, AppStatus.ACCESS_DENIED);
    }
}
