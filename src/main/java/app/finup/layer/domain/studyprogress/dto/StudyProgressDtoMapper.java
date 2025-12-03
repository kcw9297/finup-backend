package app.finup.layer.domain.studyprogress.dto;

import app.finup.layer.domain.studyprogress.entity.StudyProgress;
import lombok.*;

/**
 * 학습 진도 엔티티 클래스 -> DTO 매핑 지원 클래스
 * @author kcw
 * @since 2025-12-03
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyProgressDtoMapper {

    public static StudyProgressDto.Row toRow(StudyProgress entity) {

        return StudyProgressDto.Row.builder()
                .memberStudyId(entity.getMemberStudyId())
                .studyId(entity.getStudy().getStudyId())
                .studyStatus(entity.getStudyStatus().getValue())
                .build();
    }

}