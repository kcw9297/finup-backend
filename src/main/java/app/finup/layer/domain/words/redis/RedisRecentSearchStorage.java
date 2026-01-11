package app.finup.layer.domain.words.redis;

import java.util.List;

public interface RedisRecentSearchStorage {

    void add(Long memberId, String keyword);

    List<String> getRecent(Long memberId, Integer limit);

    void clear(Long memberId);

    void remove(Long memberId, String keyword);
}
