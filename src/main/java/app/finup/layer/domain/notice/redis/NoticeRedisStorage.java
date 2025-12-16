package app.finup.layer.domain.notice.redis;

import java.util.Map;

/**
 * 공지사항 정보를 저장할 Redis 관리 기능 제공 인터페이스
 * @author kcw
 * @since 2025-12-15
 */

public interface NoticeRedisStorage {

    /**
     * 특정 공지사항 조회수 1 증가 처리
     * @param noticeId 대상 공지글 번호
     */
    void incrementViewCount(Long noticeId);

    /**
     * 조회수 갱신이 필요한 모든 공지 조회
     * @return "공지번호 - 증가수" 쌍의 Map
     */
    Map<Long, Long> getAllIncrements();
}
