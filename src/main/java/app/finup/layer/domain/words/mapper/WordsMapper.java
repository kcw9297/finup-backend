package app.finup.layer.domain.words.mapper;

import app.finup.layer.domain.words.dto.WordsDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 단어장 검색 MyBatis Mapper 인터페이스
 * @author khj
 * @since 2025-12-13
 */

@Mapper
@Repository
public interface WordsMapper {

    List<WordsDto.Row> search(WordsDto.Search rq);

    Integer countBySearch(WordsDto.Search rq);
}
