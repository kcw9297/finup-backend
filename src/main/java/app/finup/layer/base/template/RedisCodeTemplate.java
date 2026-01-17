package app.finup.layer.base.template;

import app.finup.common.utils.StrUtils;
import app.finup.layer.domain.stock.constant.StockRedisKey;
import app.finup.layer.domain.stock.dto.StockDto;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Redis 조작 로직 중, 공용 코드를 제공하는 탬플릿 클래스
 * @author kcw
 * @since 2026-01-05
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisCodeTemplate {

    /**
     * JSON 데이터 저장 (문자열 형태로 변환 후 저장)
     * @param srt StringRedisTemplate Bean
     * @param key 저장할 Redis Key
     * @param value 저장할 Value
     * @param ttl Redis Key 만료 시간 (TimeToLive)
     */
    public static void storeJsonValue(
            StringRedisTemplate srt,
            String key,
            Object value,
            Duration ttl) {

        srt.opsForValue().set(key, StrUtils.toJson(value), ttl);
    }


    /**
     * JSON 데이터 조회 (문자열 JSON 데이터를 제공한 클래스로 역직렬화 후 변환)
     * @param srt StringRedisTemplate Bean
     * @param key 저장할 Redis Key
     * @param dtoClass 역직렬화할 클래스 타입 정보
     * @param <T> 역직렬화할 클래스 제네릭 타입
     * @return 저장된 값 (JSON -> dtoClass 역직렬화된 형태)
     */
    public static <T> T getJsonValue(
            StringRedisTemplate srt,
            String key,
            Class<T> dtoClass) {

        // [1] 조회 수행
        String value = srt.opsForValue().get(key);

        // [2] 조회 결과 반환
        return Objects.isNull(value) ? null : StrUtils.fromJson(value, dtoClass);
    }


    /**
     * 이전 데이터 목록 저장 (이전 데이터 목록을 Redis 리스트에 삽입)
     * @param srt StringRedisTemplate Bean
     * @param key 저장할 Redis Key
     * @param prevList 저장 대상 이전 데이터 목록
     * @param maxStoreAmount 리스트가 최대 보유할 수 있는 이전 데이터 개수 (개수를 초과하면 오래된 순부터 삭제)
     * @param ttl Redis Key 만료 시간 (TimeToLive)
     */
    public static void addPrevList(
            StringRedisTemplate srt,
            String key,
            List<?> prevList,
            int maxStoreAmount,
            Duration ttl
    ) {

        // 빈 리스트이거나 유효하지 않으면 로직 중단
        if (Objects.isNull(prevList) || prevList.isEmpty()) return;

        // [1] 이전 데이터 목록 데이터 타입 변환 (-> String)
        List<String> strList = prevList.stream().map(String::valueOf).toList();

        // [2] redis 내 저장
        srt.opsForList().leftPushAll(key, strList); // 리스트 왼쪽에 삽입
        srt.opsForList().trim(key, 0, maxStoreAmount - 1); // 최대 저장하는 개수
        srt.expire(key, ttl); // TTL 설정 (삽입 시마다 갱신)
    }


    /**
     * 이전 데이터 목록 조회 (String 타입으로 저장한 경우)
     * @param srt StringRedisTemplate Bean
     * @param key 저장할 Redis Key
     * @return 이전 데이터 목록 (String 목록)
     */
    public static List<String> getPrevList(
            StringRedisTemplate srt,
            String key
    ) {
        return getPrevList(srt, key, Function.identity());
    }



    /**
     * 이전 데이터 목록 조회 (String 이외 다른 타입으로 저장한 경우)
     * @param srt StringRedisTemplate Bean
     * @param key 저장할 Redis Key
     * @param castingMethod 타입 캐스팅 메소드 (String 이외 타입을 저장한 경우)
     * @param <T> castingMethod에 의해 변환될 클래스 타입
     * @return 이전 데이터 목록 (타입 변환 형태)
     */
    public static <T> List<T> getPrevList(
            StringRedisTemplate srt,
            String key,
            Function<String, T> castingMethod
    ) {

        // [1] redis 내 저장된 최근 ID 목록 일괄 조회
        List<String> values = srt.opsForList().range(key, 0L, -1L);

        // [2] 조회 결과 반환
        return Objects.isNull(values) ?
                List.of() :
                values.stream().map(castingMethod).toList();
    }


    /**
     * 이전 데이터 목록 저장 (이전 데이터 목록을 Redis 리스트에 삽입)
     * @param srt StringRedisTemplate Bean
     * @param key 저장할 Redis Key
     * @param keyValueMap Hash에 저장할 key-value 정보
     * @param ttl Redis Key 만료 시간 (TimeToLive)
     */
    public static void putAllHash (
            StringRedisTemplate srt,
            String key,
            Map<String, String> keyValueMap,
            Duration ttl
    ) {

        // [1] HashOps 조회
        HashOperations<String, Object, Object> hashOps = srt.opsForHash();

        // [2] Hash 자료구조에 key-value 값 일괄 삽입 후 ttl 갱신
        hashOps.putAll(key, keyValueMap);
        srt.expire(key, ttl);
    }


    /**
     * 이전 데이터 목록 저장 (이전 데이터 목록을 Redis 리스트에 삽입)
     * @param srt StringRedisTemplate Bean
     * @param key Hash 자료구조 Redis Key
     * @param hashKeys Hash에 저장된 key 목록 (조회 대상)
     * @param dtoClass 변환할 DTO 클래스 정보
     */
    public static <T> List<T> getMultiHash(
            StringRedisTemplate srt,
            String key,
            Collection<?> hashKeys,
            Class<T> dtoClass
    ) {

        // [1] 일괄 조회
        List<Object> values = srt.opsForHash().multiGet(key, new ArrayList<>(hashKeys));

        // [2] DTO로 변환 및 반환
        return values.stream()
                .filter(Objects::nonNull)
                .map(value -> StrUtils.fromJson((String) value, dtoClass))
                .toList();
    }


    /**
     * 이전 데이터 목록 저장 (이전 데이터 목록을 Redis 리스트에 삽입)
     * @param srt StringRedisTemplate Bean
     * @param key Hash 자료구조 Redis Key
     * @param dtoClass 변환할 DTO 클래스 정보
     */
    public static <T> List<T> getAllHash(
            StringRedisTemplate srt,
            String key,
            Class<T> dtoClass
    ) {

        return srt.opsForHash().entries(key)
                .entrySet()
                .stream()
                .filter(Objects::nonNull)
                .map(entry -> StrUtils.fromJson((String) entry.getValue(), dtoClass))
                .toList();
    }







}
