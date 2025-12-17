package app.finup.common.advice;

import app.finup.common.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class BaseAdvice extends ResponseEntityExceptionHandler {

    /**
     * HTTP Method 미지원 시 처리 예외
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("지원하지 않는 HTTP 메소드: {} - {}", ex.getMethod(), request.getDescription(false));
        return Api.fail(AppStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * HTTP 요청 내 Message를 읽지 못한 경우
     * (@RequestBody JSON -> DTO 변환이 실패한 경우)
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("JSON -> DTO 파싱 실패! : {}", ex.getMessage());
        return Api.fail(AppStatus.SERVER_ERROR);
    }


    /**
     * HTTP 응답 내 Message를 쓰지 못한 경우
     * (@ResponseBody DTO -> JSON 변환이 실패한 경우)
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("DTO -> JSON 파싱 실패! : {}", ex.getMessage());
        return Api.fail(AppStatus.SERVER_ERROR);
    }


    /**
     * Content-Type 불일치로 인한 예외 처리
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("지원하지 않는 Media Type: {}", ex.getContentType());
        return Api.fail(AppStatus.SERVER_ERROR);
    }


    /**
     * DTO 대상 bean validation 실패 예외 처리
     * (@RequestBody 로 받아온 DTO 객체를 @Valid or @Validated 로 검증 실패 시)
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        // [1] 발생 오류 추출
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.isNull(fieldError.getDefaultMessage()) ? "올바른 값을 입력해 주세요." : fieldError.getDefaultMessage(),
                        (msg1, msg2) -> msg1 // 중복 시 첫 번째 유지
                ));

        // [2] 유효성 검사 실패 응답 반환
        log.error("DTO 유효성 검사 실패 : {}", errors);
        return Api.fail(errors);
    }


    /**
     * 단일 메소드 파라미터 대상 bean validation 실패 예외 처리
     * 클래스 레벨에 @Validated 를 생략해서 발생하기도 함
     * (@PathVariable, @RequestParam 등 단일 파라미터 검증 실패 시)
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> handleBeanValidationEx(ConstraintViolationException e) {

        // [1] 발생 오류 추출
        Map<String, String> errors = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> {
                            String[] parts = v.getPropertyPath().toString().split("\\.");
                            return parts[parts.length - 1]; // 가장 마지막 필드명
                        },
                        ConstraintViolation::getMessage,
                        (old, latest) -> old // 중복 필드 있을 경우 최초정보 유지
                ));

        // [2] 유효성 검사 실패 응답 반환
        log.error("DTO 이외 파라미터 유효성 검사 실패 : {}", errors);
        return Api.fail(errors);
    }


    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<?> handleBusinessEx(BusinessException e) {
        log.error("비즈니스 로직 실패! : {}", e.getAppStatus().getInfo());
        return Api.fail(e.getAppStatus(), e.getInputErrors());
    }


    /**
     * Manager, Provider 실패 시
     */
    @ExceptionHandler(value = {ManagerException.class, ProviderException.class})
    public ResponseEntity<?> handleManaProvEx(AppException e) {

        log.error("Manager/Provider 로직 실패! : {}", e.getAppStatus().getInfo());
        return Api.fail(e.getAppStatus(), e.getInputErrors());
    }


    /**
     * JWT 검증 실패 예외 처리
     */
    @ExceptionHandler(value = JwtVerifyException.class)
    public ResponseEntity<?> handleJwtVerifyEx(JwtVerifyException e) {

        log.error("JWT 검증 실패 : {}", e.getAppStatus().getInfo());
        return Api.fail(e.getAppStatus());
    }


    /**
     * 그 밖의 전역적 예외 처리
     */
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<?> handleAppEx(AppException e) {

        log.error("비즈니스 이외의 로직 실패! : {}", e.getAppStatus().getInfo());
        return Api.fail(e.getAppStatus());
    }


    /**
     * 예기치 않은 예외 상황 처리
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleOtherEx(Exception e) {

        log.error("예상 외 오류 발생! : {}", e.getMessage(), e);
        return Api.fail(AppStatus.SERVER_ERROR);
    }
}
