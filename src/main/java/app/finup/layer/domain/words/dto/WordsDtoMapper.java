package app.finup.layer.domain.words.dto;

import app.finup.layer.domain.words.entity.Words;
import lombok.*;

/**
 * Finance Dictionary Entity -> DTO 매퍼 클래스
 * @author khj
 * @since 2025-12-10
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WordsDtoMapper {

    public static WordsDto.Row toRow(Words entity) {
        return WordsDto.Row.builder()
                .termId(entity.getTermId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

}
