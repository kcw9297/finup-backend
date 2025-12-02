package app.finup.layer.domain.reboard.dto;

import app.finup.layer.domain.reboard.entity.Reboard;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Reboard Entity -> DTO 매퍼 클래스
 * @author kcw
 * @since 2025-11-24
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReboardDtoMapper {

    public static ReboardDto.Detail toDetail(Reboard entity) {

        return ReboardDto.Detail.builder()
                .idx(entity.getIdx())
                .name(entity.getName())
                .subject(entity.getSubject())
                .content(entity.getContent())
                .regdate(entity.getRegdate())
                .build();
    }

    public static ReboardDto.Row toRow(Reboard entity) {

        return ReboardDto.Row.builder()
                .idx(entity.getIdx())
                .name(entity.getName())
                .subject(entity.getSubject())
                .regdate(entity.getRegdate())
                .build();
    }

}
