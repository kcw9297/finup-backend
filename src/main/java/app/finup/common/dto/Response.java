package app.finup.common.dto;

import app.finup.common.enums.AppStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * REST API 응답 스펙 클래스
 * @param <T> 응답 데이터 클래스 타입
 */

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Response<T> {

    private Boolean success;        // 로직 성공/실패 여부
    private String message;         // 응답 메세지
    private String status;          // 응답 상태 값
    private Integer statusCode;     // HttpStatusCode (200, 400, 500, ...)
    private T data;                 // 응답 데이터 (현재는 로직 성공시에만 전달)
    private Pagination pagination;  // 페이징 메타데이터 (응답 데이터가 페이징된 경우)
    private Map<String, String> inputErrors; // Bean Validation 실패 시 전달할 [에러 발생 필드 - 에러 메세지] Map

    /**
     * 성공 상태만을 전달
     */
    public static Response<?> ok() {
        return ok(AppStatus.OK);
    }

    /**
     * 성공 상태 및 Status 전달
     * @param appStatus 애플레케이션 상태 상수
     */
    public static Response<?> ok(AppStatus appStatus) {
        return ok(appStatus, null, null);
    }

    /**
     * 성공 상태 및 응답 데이터 전달
     * @param data 응답 데이터
     */
    public static <T> Response<T> ok(T data) {
        return ok(AppStatus.OK, data, null);
    }

    /**
     * 성공 상태 및 응답 데이터 & 페이징 메타데이터 전달
     * @param data 응답 데이터
     * @param pagination 페이징 메타데이터
     */
    public static <T> Response<T> ok(T data, Pagination pagination) {
        return ok(AppStatus.OK, data, pagination);
    }

    /**
     * 성공 상태 및 Status, 데이터 전달
     * @param appStatus 애플레케이션 상태 상수
     * @param data 응답 데이터
     */
    public static <T> Response<T> ok(AppStatus appStatus, T data) {
        return ok(appStatus, data, null);
    }

    /**
     * 성공 상태 및 Status, 데이터 전달
     * @param appStatus 애플레케이션 상태 상수
     * @param data 응답 데이터
     * @param pagination 페이징 메타데이터
     */
    public static <T> Response<T> ok(AppStatus appStatus, T data, Pagination pagination) {
        return new Response<>(true, appStatus.getMessage(), appStatus.getStatus(), appStatus.getHttpCode(), data, pagination, null);
    }

    /**
     * 실패 상태, 메세지, 오류코드 전달 (메세지를 직접 작성하는 경우)
     *
     * @param errorMessage  오류 메세지
     * @param status        오류 상태 문자열 (INVALID_REQUEST, ...)
     * @param statusCode    오류 코드 (400, 401, 403, 500, ...)
     */
    public static Response<?> fail(String errorMessage, String status, int statusCode) {
        return new Response<>(false, errorMessage, status, statusCode, null, null, null);
    }

    /**
     * 실패 상태 및 status 전달
     * @param appStatus 애플레케이션 상태 상수
     */
    public static Response<?> fail(AppStatus appStatus) {
        return new Response<>(false, appStatus.getMessage(), appStatus.getStatus(), appStatus.getHttpCode(), null, null, null);
    }

    /**
     * 실패 상태 및 inputErrors 전달 (bean validation 실패 처리 시 사용)
     * @param inputErrors bean validation 실패 시 반환되는 map
     */
    public static Response<?> fail(Map<String, String> inputErrors) {
        return new Response<>(
                false,
                AppStatus.VALIDATION_INVALID_PARAMETER.getMessage(),
                AppStatus.VALIDATION_INVALID_PARAMETER.getStatus(),
                AppStatus.VALIDATION_INVALID_PARAMETER.getHttpCode(),
                null,
                null,
                inputErrors
        );
    }
}