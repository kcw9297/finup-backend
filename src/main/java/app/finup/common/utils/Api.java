package app.finup.common.utils;

import jakarta.servlet.http.HttpServletResponse;
import app.finup.common.dto.Pagination;
import app.finup.common.dto.Response;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.UtilsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * REST API 응답 전용 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Api {

    /**
     * 직접 JSON 성공 응답을 전송하는 경우 (주로 Spring Security Filter 내 사용)
     * @param response 서블릿 응답 객체
     * @param appStatus 애플리케이션 상태 상수
     */
    public static void writeOK(HttpServletResponse response, AppStatus appStatus) {

        try {
            writeJson(response, HttpServletResponse.SC_OK, StrUtils.toJson(Response.ok(appStatus)));

        } catch (Exception e) {
            log.error("JSON 200 OK 응답처리 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }

    /**
     * 직접 JSON 실패 응답을 전송하는 경우 (주로 Spring Security Filter 내 사용)
     * @param response 서블릿 응답 객체
     * @param appStatus 애플리케이션 상태 상수
     */
    public static void writeFail(HttpServletResponse response, AppStatus appStatus) {

        try {
            writeJson(response, appStatus.getHttpCode(), StrUtils.toJson(Response.fail(appStatus)));

        } catch (Exception e) {
            log.error("JSON 오류 응답처리 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }

    // JSON 응답 전송 수행
    private static void writeJson(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(message);
    }

    /**
     * 순수 성공상태 응답
     */
    public static ResponseEntity<Object> ok() {
        return ok(AppStatus.OK);
    }

    /**
     * 성공 상태 및 Status 전달
     * @param appStatus 애플레케이션 상태 상수
     */
    public static ResponseEntity<Object> ok(AppStatus appStatus) {
        return ok(appStatus, null, null);
    }

    /**
     * 성공 상태 및 응답 데이터 전달
     * @param data 응답 데이터
     */
    public static <T> ResponseEntity<Object> ok(T data) {
        return ok(data, null);
    }

    /**
     * 성공 상태 및 응답 데이터 & 페이징 메타데이터 전달
     * @param data 응답 데이터
     * @param pagination 페이징 메타데이터
     */
    public static <T> ResponseEntity<Object> ok(T data, Pagination pagination) {
        return ok(AppStatus.OK, data, pagination);
    }

    /**
     * 성공 상태 및 Status, 데이터 전달
     * @param appStatus 애플레케이션 상태 상수
     * @param data 응답 데이터
     */
    public static <T> ResponseEntity<Object> ok(AppStatus appStatus, T data) {
        return ok(appStatus, data, null);
    }

    /**
     * 성공 상태 및 Status, 데이터 전달
     * @param appStatus 애플레케이션 상태 상수
     * @param data 응답 데이터
     * @param pagination 페이징 메타데이터
     */
    public static <T> ResponseEntity<Object> ok(AppStatus appStatus, T data, Pagination pagination) {
        return new ResponseEntity<>(Response.ok(appStatus, data, pagination), HttpStatus.OK);
    }

    /**
     * 실패 상태, 메세지, 오류코드 전달 (메세지를 직접 작성하는 경우)
     * @param errorMessage 오류 메세지
     * @param statusCode 오류 코드 (400, 401, 403, 500, ...)
     */
    public static ResponseEntity<Object> fail(String errorMessage, String status, int statusCode) {
        return new ResponseEntity<>(Response.fail(errorMessage, status, statusCode), HttpStatusCode.valueOf(statusCode));
    }

    /**
     * 실패 상태 및 status 전달
     * @param appStatus 애플레케이션 상태 상수
     */
    public static ResponseEntity<Object> fail(AppStatus appStatus) {
        return new ResponseEntity<>(Response.fail(appStatus), getStatusCode(appStatus));
    }

    /**
     * 실패 상태 및 inputErrors 전달 (bean validation 실패 처리 시 사용)
     * @param inputErrors bean validation 실패 시 반환되는 map
     */
    public static ResponseEntity<Object> fail(Map<String, String> inputErrors) {
        return new ResponseEntity<>(Response.fail(inputErrors), getStatusCode(AppStatus.VALIDATION_INVALID_PARAMETER));
    }


    // AppStatus 내 상태코드 확인 후 대응하는 HttpStatus 반환
    private static HttpStatus getStatusCode(AppStatus appStatus) {
        HttpStatus status = HttpStatus.resolve(appStatus.getHttpCode());
        return Objects.isNull(status) ? HttpStatus.INTERNAL_SERVER_ERROR : status;
    }

}
