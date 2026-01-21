package app.finup.api.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * API 응답에 사용하는 유틸 기능 제공 클래스
 * @author kcw
 * @since 2026-01-15
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiUtils {


    /**
     * API 결과로 얻은 문자열 검증
     * @param condition API 응답 문자열
     * @param data 검증 시도 데이터
     * @param failStatus 실패 시 처리할 AppStatus
     * @return 검증 성공 처리한 Mono
     */
    public static <T> Mono<T> validate(Predicate<T> condition, T data, AppStatus failStatus) {

        return condition.test(data) ?
                Mono.just(data) :
                Mono.error(new ProviderException(failStatus));
    }


    /**
     * API 결과로 얻은 문자열 검증
     * @param result API 응답 문자열
     * @return 검증 성공 처리한 Mono
     */
    public static Mono<String> validateEmpty(String result) {

        // [1] 1차적으로 문자열 자체가 유효한지 검증
        if (Objects.isNull(result) || result.isBlank())
            return Mono.error(new ProviderException(AppStatus.API_RESPONSE_EMPTY));

        // [2] JSON 문자열 반환인 경우, 빈 배열이나 객체인지 검증
        String trimmed = result.trim();
        if ("[]".equals(trimmed) || "{}".equals(trimmed))
            return Mono.error(new ProviderException(AppStatus.API_RESPONSE_EMPTY));

        // [3] 정상 통과 시 원본 결과 반환
        return Mono.just(result);
    }


    /**
     * API 결과로 얻은 문자열 검증
     * @param result    API 응답 결과 객체
     * @param condition 만족해야 할 조건
     * @return 검증 성공 처리한 Mono
     */
    public static <T> Mono<T> validateCode(T result, Predicate<T> condition) {

        return condition.test(result) ?
                Mono.just(result) : // 성공한 경우 통과
                Mono.error(new ProviderException(AppStatus.API_RESPONSE_CODE_ERROR)); // 실패한 경우 오류
    }


    /**
     * 리스트 내 모든 항목의 조건 검증
     * @param resultList API 응답 결과 객체 리스트
     * @param condition 각 항목이 만족해야 할 조건
     */
    public static <T> Mono<List<T>> validateAllCode(List<T> resultList, Predicate<T> condition) {

        // [1] DTO List를 순회하며 조건을 만족하는지 판단
        boolean allValid = resultList.stream().allMatch(condition);

        // [2] 모두 유효하면 객체 반환
        return allValid ?
                Mono.just(resultList) :
                Mono.error(new ProviderException(AppStatus.API_RESPONSE_CODE_ERROR));
    }

}
