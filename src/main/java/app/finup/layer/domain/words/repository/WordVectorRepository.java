package app.finup.layer.domain.words.repository;

import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.dto.WordsDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WordVectorRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * word의 벡터를 word_vector 테이블에 저장
     */
    public void upsert(Long termId, String embeddingJson){
        jdbcTemplate.update("""
            INSERT INTO words_vector (term_id, embedding)
            VALUES (?, VEC_FromText(?))
            ON DUPLICATE KEY UPDATE embedding = VALUES(embedding)
        """, termId, embeddingJson);
    }

    /**
     * 입력 벡터와 가장 유사한 단어 검색
     */
    public List<WordsDto.Similarity> search(String queryEmbeddingJson, int topK){
        return jdbcTemplate.query("""
                SELECT w.term_id, w.name, w.description,
                VEC_DISTANCE_COSINE(v.embedding, VEC_FromText(?)) AS score
                FROM words_vector v
                JOIN words w ON w.term_id = v.term_id
                WHERE VEC_DISTANCE_COSINE(v.embedding, VEC_FromText(?)) IS NOT NULL
                ORDER BY score
                LIMIT ?
        """, similarityMapper, queryEmbeddingJson, queryEmbeddingJson, topK);
    }

    private final RowMapper<WordsDto.Similarity> similarityMapper =
            (rs, rowNum) -> WordsDtoMapper.toSimilarity(
                    rs.getLong("term_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getObject("score", Double.class)
            );
}
