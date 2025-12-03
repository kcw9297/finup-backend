package app.finup.layer.domain.study.dto;

import app.finup.layer.domain.study.entity.Study;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class StudyDtoMapper {

    public static StudyDto.Row toRow(Study entity) {

        return StudyDto.Row.builder()
                .studyId(entity.getStudyId())
                .name(entity.getName())
                .summary(entity.getSummary())
                .level(entity.getLevel())
                .build();
    }

    public static StudyDto.Detail toDetail(Study entity) {

        return StudyDto.Detail.builder()
                .studyId(entity.getStudyId())
                .name(entity.getName())
                .summary(entity.getSummary())
                .description(entity.getDescription())
                .level(entity.getLevel())
                .build();
    }

}
