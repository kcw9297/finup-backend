package app.finup.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.UtilsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 문자열 관련 조작 기능 제공 유틸 클래스
 * @author kcw
 * @since 2025-11-21
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StrUtils {

    // 내부 상수
    private static final Random random = new Random();
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder();

    @lombok.Getter
    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    /**
     * JSON 직렬화 (데이터를 JSON 문자열로 변경)
     * @param data 직렬화 대상 데이터 객체
     * @return JSON 문자열
     */
    public static String toJson(Object data) {

        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("JSON 직렬화 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }


    /**
     * JSON 역직렬화 (JSON 문자열을 원래 타입으로 변경)
     * @param json 역직렬화 대상 JSON 문자열
     * @param typeReference 변환활 타입 정보 객체
     * @return 원래 타입으로 변환된 데이터
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("JSON 역직렬화 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }


    /**
     * JSON 역직렬화 (JSON 문자열을 원래 타입으로 변경)
     * @param json 역직렬화 대상 JSON 문자열
     * @param type 변환활 타입 정보 객체
     * @return 원래 타입으로 변환된 데이터
     */
    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.error("JSON 역직렬화 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }


    /**
     * JSON 리스트 역직렬화 (JSON 문자열을 원래 타입으로 변경)
     * @param json 역직렬화 대상 JSON 문자열
     * @param elementType List 내 원소 타입
     * @return 원래 타입으로 변환된 데이터 목록
     */
    public static <T> List<T> fromJsonList(String json, Class<T> elementType) {
        try {
            JavaType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementType);
            return objectMapper.readValue(json, listType);

        } catch (Exception e) {
            log.error("JSON 역직렬화 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }



    /**
     * 랜덤 숫자 문자열 생성
     * @param length 생성할 문자열 길이
     * @return 랜덤 생성된 숫자 문자열 (ex. 000123)
     */
    public static String createRandomNumString(int length) {

        // [1] 숫자 format, 랜덤 수 범위 계산
        String format = "%0" + length + "d";
        int randomRange = (int) Math.pow(10, length); // 10^length

        // [2] 랜덤 수 생성 및 반환
        return format.formatted(random.nextInt(randomRange));
    }


    /**
     * 랜덤 숫자 문자열 생성 (prefix 포함)
     * @param length 생성할 문자열 길이
     * @param prefix 생성할 숫자 문자열 앞에 올 문자열
     * @return 랜덤 생성된 숫자 문자열 (ex. ID000123)
     */
    public static String createRandomNumString(int length, String prefix) {

        // [1] 숫자 format, 랜덤 수 범위 계산
        String format = "%s%0" + length + "d";
        int randomRange = (int) Math.pow(10, length); // 10^length

        // [2] 랜덤 수 생성 및 반환
        return format.formatted(prefix, random.nextInt(randomRange));
    }


    /**
     * UUID 문자열 생성
     * @return UUID 문자열 ('-' 삭제)
     */
    public static String createUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 22자리 UUID 문자열 생성
     * @return 22자리 UUID 문자열 ('-' 삭제)
     */
    public static String createShortUUID() {

        // [1] 랜덤 문자열 생성 (UUID)
        UUID uuid = UUID.randomUUID();

        // [2] byte 배열로 변환
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] bytes = new byte[16];
        for (int i = 0; i < 8; i++) {
            bytes[i]     = (byte)(msb >>> 8 * (7 - i));
            bytes[8 + i] = (byte)(lsb >>> 8 * (7 - i));
        }

        // [3] 22자 길이의 UUID로 인코딩 후 반환
        return urlEncoder.withoutPadding().encodeToString(bytes);
    }


    /**
     * IPv6 -> IPv4 변환
     * @param ip 변환 대상 ip 문자열
     * @return IPv4 문자열
     */
    public static String getIPv4ClientIp(String ip){
        try {

            // [1] ip 형태 구분을 위한 객체 생성
            InetAddress inet = InetAddress.getByName(ip);

            // [2] 유형 구분 후 변환 및 반환
            // Local 환경에서 IPv6 주소 변환
            if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) return "127.0.0.1";

            // IPv4-mapped IPv6 주소 (::ffff:x.x.x.x) -> x.x.x.x
            if (ip.startsWith("::ffff:")) return ip.substring(7);

            // [순수 IPv6 는 변환 불가 -> ex. UNKNOWN_IPv6(2404:6800:4001::200e)
            if (inet instanceof Inet6Address) return "UNKNOWN_IPv6(%s)".formatted(ip);

            // IPv4 형태는 처리하지 않고 그대로 반환
            return inet.getHostAddress();

        } catch (UnknownHostException e) {
            log.error("아이피 조회 실패. 오류 : {}", e.getMessage());
            return ip;
        }
    }


    /**
     * 랜덤 version 문자열 생성 (파일 경로 뒤에 추가)
     * @param fileUrl 파일 경로
     * @return 버전 정보가 포함된 파일 경로 문자열
     */
    public static String createVersionUrl(String fileUrl) {
        return "%s?v=%d".formatted(fileUrl, System.currentTimeMillis());
    }


    /**
     * 파일 확장자 추출
     * @param filename 대상 파일명
     */
    public static String getFileExt(String filename) {

        // 만약 유효하지 않은 파일명이면 null
        if (Objects.isNull(filename)) return null;

        // 파일 확장자가 있는 인덱스
        int extIdx = filename.lastIndexOf('.');

        // 파일 확장자가 존재하는 경우에만, 추출 후 반환
        return extIdx == -1 || extIdx == filename.length() - 1 ?
                "" : filename.substring(extIdx + 1);
    }


    /**
     * 문자열 시작부터 일정 길이만큼 문자열 자름(split)
     * @param text 대상 문자열
     * @param splitLen 자를 문자열 위치 (해당 길이를 이후의 문자열을 자르고 '...' 으로 대체)
     * @return split 처리한 문자열 (ex. 문자열...)
     */
    public static String splitWithStart(String text, int splitLen) {

        return Objects.isNull(text) ? null :
                text.length() > splitLen ? text.substring(0, splitLen) + "..." : text;
    }


    /**
     * 문자열 내 줄바꿈 제거 및 과도한 띄어 쓰기(공백 제거)
     * @param text 대상 문자열
     * @return split 처리한 문자열 (ex. 문자열...)
     */
    public static String removeEmptySpace(String text) {

        return Objects.isNull(text) ? null :
                text.replaceAll("[\\r\\n]+", " ")  // 모든 줄바꿈을 공백으로 변환
                .replaceAll("\\s+", " ")       // 연속된 공백을 하나로 압축
                .trim();
    }







    /**
     * 문자열 내 placeholder 값 대체 (ex. ${INPUT} 값을 대체)
     * @param text 대상 문자열 (placeholder 포함)
     * @param placeholders Map<Placeholder, 대체값>
     * @return placeholder 값을 채운 문자열
     */
    public static String fillPlaceholder(String text, Map<String, String> placeholders) {

        // 결과 탬플릿
        String result = text;

        // 탬플릿 내 Placeholder($) 값 채움
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = Objects.isNull(entry.getValue()) ? "" : entry.getValue();
            result = result.replace(entry.getKey(), value);
        }

        // 결과 반환
        return result;
    }


    /**
     * 해당 문자열이 유효한지 검증 (null이거나 공백이 아님)
     * @param text 대상 문자열
     * @return 검증 결과 (false - 유효하지 않음)
     */
    public static boolean isValid(String text) {
        return Objects.nonNull(text) && !text.isBlank();

    }
}