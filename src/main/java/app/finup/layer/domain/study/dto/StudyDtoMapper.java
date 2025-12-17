package app.finup.layer.domain.study.dto;

import app.finup.layer.domain.study.entity.Study;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 단계 학습 Entity -> DTO 매핑 지원 클래스
 * @author kcw
 * @since 2025-12-03
 */

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class StudyDtoMapper {

    public static StudyDto.Row toRow(Study entity) {

        return StudyDto.Row.builder()
                .studyId(entity.getStudyId())
                .name(entity.getName())
                .summary(entity.getSummary())
                .detail(entity.getDetail())
                .level(entity.getLevel())
                .build();
    }

    public static StudyDto.Detail toDetail(Study entity) {

        return StudyDto.Detail.builder()
                .studyId(entity.getStudyId())
                .name(entity.getName())
                .summary(entity.getSummary())
                .detail(entity.getDetail())
                .level(entity.getLevel())
                .build();
    }

}
