package app.finup.layer.domain.words.repository;

import app.finup.layer.domain.words.dto.WordsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WordsJdbcRepository 구현 클래스
 * @author kcw
 * @since 2025-02-05
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class WordsJdbcRepositoryImpl implements WordsJdbcRepository {

    // 사용 의존성
    private final JdbcTemplate jdbcTemplate;

    // 사용 상수
    private static final String TERM_ID = "term_id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    // 사용 쿼리
    @SuppressWarnings("SqlResolve")
    private static final String SQL_FIND_WITH_SIMILAR = """
        SELECT term_id, name, description
        FROM words
        WHERE embedding IS NOT NULL
          AND (
                name LIKE CONCAT('%', ?, '%') OR
                (description LIKE CONCAT('%', ?, '%') AND VEC_DISTANCE_COSINE(embedding, ?) <= 0.70) OR
                VEC_DISTANCE_COSINE(embedding, ?) <= 0.50
            )
        ORDER BY
          CASE
            WHEN name = ? THEN 0
            WHEN name LIKE CONCAT(?, '%') THEN 1
            WHEN name LIKE CONCAT('%', ?, '%') THEN 2
            WHEN description LIKE CONCAT('%', ?, '%') THEN 3
            ELSE 4
          END,
          VEC_DISTANCE_COSINE(embedding, ?) -- 유사도 순
        LIMIT ?
    """;


    @Override
    public List<WordsDto.Row> findWithSimilarByKeyword(String keyword, byte[] embedding, int lim) {
        return jdbcTemplate.query(SQL_FIND_WITH_SIMILAR,
                (rs, rowNum) -> WordsDto.Row.builder()
                        .termId(rs.getLong(TERM_ID))
                        .name(rs.getString(NAME))
                        .description(rs.getString(DESCRIPTION))
                        .build(),
                keyword, keyword, embedding, embedding, keyword, keyword, keyword, keyword, embedding, lim
        );
    }
}
