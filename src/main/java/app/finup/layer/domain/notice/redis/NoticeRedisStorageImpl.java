package app.finup.layer.domain.notice.redis;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NoticeRedisStorage 인터페이스 구현체
 * @author kcw
 * @since 2025-12-15
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeRedisStorageImpl implements NoticeRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final String KEY_PREFIX = "NOTICE:";
    private static final String KEY_INCREMENT = "INCREMENT:";

    @Value("${app.redis.scan-count}")
    private Integer scanCount;


    @Override
    public void incrementViewCount(Long noticeId) {

        // [1] key 생성
        String key = "%s%s%s".formatted(KEY_PREFIX, KEY_INCREMENT, noticeId);

        // [2] 조회수 정보를 value 값으로 저장 (1 증가 처리)
        srt.opsForValue().increment(key, 1);
    }


    @Override
    public Map<Long, Long> getAllIncrements() {

        // [1] 조회 데이터를 담을 map 및 검색 pattern 정의
        Map<Long, Long> incrementMap = new ConcurrentHashMap<>();
        String pattern = "%s%s*".formatted(KEY_PREFIX, KEY_INCREMENT);

        // [2] scan 옵션 생성
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(scanCount) // 한 번에 검색할 key 개수
                .build();

        // [3] key 조회 (패턴에 일치하는 패턴만)
        try (Cursor<String> cursor = srt.scan(options)) {
            while (cursor.hasNext()) {

                // 다음 커서로 이동
                String key = cursor.next();

                // noticeId 추출
                String idStr = key.replace(KEY_PREFIX + KEY_INCREMENT, "");
                Long noticeId = Long.parseLong(idStr);

                // 값 조회 후, 만약 비어있지 않으면 결과 Map에 삽입
                String value = srt.opsForValue().getAndDelete(key); // 값을 조회한 후 즉시 삭제
                if (Objects.nonNull(value)) incrementMap.put(noticeId, Long.parseLong(value));
            }
        }

        return incrementMap;
    }


}
