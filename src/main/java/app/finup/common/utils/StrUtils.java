package app.finup.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.UtilsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriUtils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;

/**
 * 문자열 관련 조작 기능 제공 유틸 클래스
 * @author kcw
 * @since 2025-11-21
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StrUtils {

    private static final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Random random = new Random();
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder();

    public static String toJson(Object data) {

        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("JSON 직렬화 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }


    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("JSON 역직렬화 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.error("JSON 역직렬화 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }



    public static String encodeToUTF8(String text) {

        try {
            return UriUtils.encode(text, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("UTF8 인코딩 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }


    public static String decodeToUTF8(String text) {

        try {
            return UriUtils.decode(text, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("UTF8 인코딩 실패. 오류 : {}", e.getMessage());
            throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }


    public static String createRandomNumString(int length) {

        // [1] 숫자 format, 랜덤 수 범위 계산
        String format = "%0" + length + "d";
        int randomRange = (int) Math.pow(10, length); // 10^length

        // [2] 랜덤 수 생성 및 반환
        return format.formatted(random.nextInt(randomRange));
    }

    public static String createRandomNumString(int length, String prefix) {

        // [1] 숫자 format, 랜덤 수 범위 계산
        String format = "%s%0" + length + "d";
        int randomRange = (int) Math.pow(10, length); // 10^length

        // [2] 랜덤 수 생성 및 반환
        return format.formatted(prefix, random.nextInt(randomRange));
    }


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

    // IPv6 -> IPv4 변환
    public static String getIPv4ClientIp(String clientIp){
        try {

            // [1] ip 형태 구분을 위한 객체 생성
            InetAddress inet = InetAddress.getByName(clientIp);

            // [2] 유형 구분 후 변환 및 반환
            // Local 환경에서 IPv6 주소 변환
            if ("0:0:0:0:0:0:0:1".equals(clientIp) || "::1".equals(clientIp)) return "127.0.0.1";

            // IPv4-mapped IPv6 주소 (::ffff:x.x.x.x) -> x.x.x.x
            if (clientIp.startsWith("::ffff:")) return clientIp.substring(7);

            // [순수 IPv6 는 변환 불가 -> ex. UNKNOWN_IPv6(2404:6800:4001::200e)
            if (inet instanceof Inet6Address) return "UNKNOWN_IPv6(%s)".formatted(clientIp);

            // IPv4 형태는 처리하지 않고 그대로 반환
            return inet.getHostAddress();

        } catch (UnknownHostException e) {
            log.error("아이피 조회 실패. 오류 : {}", e.getMessage());
            return clientIp;
        }
    }

    public static String createUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String createVersionUrl(String fileUrl) {
        return "%s?v=%d".formatted(fileUrl, System.currentTimeMillis());
    }

    public static boolean equalsStatus(AppStatus status1, AppStatus status2) {
        return Objects.equals(status1.getStatus(), status2.getStatus());
    }


    /**
     * 파일 확장자 추출
     * @param filename 대상 파일명
     */
    public static String getFileExt(String filename) {

        // 파일 확장자가 있는 인덱스
        int extIdx = filename.lastIndexOf('.');

        // 파일 확장자가 존재하는 경우에만, 추출 후 반환
        return extIdx == -1 || extIdx == filename.length() - 1 ?
                "" : filename.substring(extIdx + 1);
    }


    public static String splitWithStart(String text, int splitDescriptionLen) {

        return Objects.isNull(text) ? null :
                text.length() > splitDescriptionLen ? text.substring(0, splitDescriptionLen) + "..." : text;
    }

    // Markdown 코드 블록 제거
    public static String removeMarkdownBlock(String text) {

        if (Objects.isNull(text) || text.isBlank()) {
            return "";
        }

        return text
                // [1] Markdown 코드블록 제거 (```언어\n내용\n``` 형태)
                .replaceAll("```[a-z]*\\n?", "")
                .replaceAll("```", "")

                // [2] 단일 백틱 제거 (`내용` 형태)
                .replaceAll("^`|`$", "")

                // [3] JSON 따옴표 제거 (전체가 "내용" 형태)
                .replaceAll("^\"", "")
                .replaceAll("\"$", "")

                // [4] 불필요한 접두어 제거
                .replaceAll("^(분석|결과|답변|응답|해설)\\s*:\\s*", "")

                // [5] 앞뒤 공백 제거
                .trim();
    }


    public static String fillPlaceholder(String text, Map<String, String> placeholders) {

        // 결과 탬플릿
        String result = text;

        // 탬플릿 내 Placeholder($) 값 채움
        for (Map.Entry<String, String> entry : placeholders.entrySet())
            result = result.replace(entry.getKey(), entry.getValue());

        // 결과 반환
        return result;
    }

}