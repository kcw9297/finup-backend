package app.finup.layer.domain.reboard.dto;

import app.finup.layer.domain.reboard.entity.Reboard;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

    public static ReboardDto.Summary toSummary(Reboard entity) {

        return ReboardDto.Summary.builder()
                .idx(entity.getIdx())
                .name(entity.getName())
                .subject(entity.getSubject())
                .regdate(entity.getRegdate())
                .build();
    }

}
