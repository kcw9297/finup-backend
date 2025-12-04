package app.finup.layer.domain.studyword.dto;

import app.finup.layer.domain.studyword.entity.StudyWord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

/**
 * 학습 단어 Entity -> DTO 매핑 지원 클래스
 * @author kcw
 * @since 2025-12-03
 */

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class StudyWordDtoMapper {

    public static StudyWordDto.Row toRow(StudyWord entity, Function<String, String> urlResolver) {

        return StudyWordDto.Row.builder()
                .studyWordId(entity.getStudyWordId())
                .name(entity.getName())
                .meaning(entity.getMeaning())
                .imageUrl(urlResolver.apply(entity.getWordImageFile().getFilePath()))
                .build();
    }

}
