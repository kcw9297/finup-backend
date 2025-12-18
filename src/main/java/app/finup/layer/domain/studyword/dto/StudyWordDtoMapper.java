package app.finup.layer.domain.studyword.dto;

import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.studyword.entity.StudyWord;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 학습 단어 Entity -> DTO 매핑 지원 클래스
 * @author kcw
 * @since 2025-12-03
 */

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class StudyWordDtoMapper {

    public static StudyWordDto.Row toRow(StudyWord entity) {

        UploadFile wordImageFile = entity.getWordImageFile();

        return StudyWordDto.Row.builder()
                .studyWordId(entity.getStudyWordId())
                .name(entity.getName())
                .meaning(entity.getMeaning())
                .imageUrl(Objects.isNull(wordImageFile) ? null : wordImageFile.getFilePath())
                .build();
    }


    public static StudyWordDto.Quiz toQuiz(StudyWord entity) {

        return StudyWordDto.Quiz.builder()
                .name(entity.getName())
                .meaning(entity.getMeaning())
                .build();
    }


    public static StudyWordDto.Recommendation toRecommendation(Study entity, Collection<StudyWordDto.Row> candidates, List<Long> latestStudyWordIds) {

        // [1] 학습 데이터
        StudyWordDto.Recommendation.StudyInfo study =
                StudyWordDto.Recommendation.StudyInfo.builder()
                        .name(entity.getName())
                        .summary(entity.getSummary())
                        .detail(entity.getDetail())
                        .level(entity.getLevel())
                        .build();

        // [2] 영상 데이터
        List<StudyWordDto.Recommendation.WordCandidate> wordCandidates =
                candidates.stream()
                        .map(candidate -> StudyWordDto.Recommendation.WordCandidate.builder()
                                .studyWordId(candidate.getStudyWordId())
                                .name(candidate.getName())
                                .meaning(candidate.getMeaning())
                                .build()
                        ).toList();

        // [3] DTO 반환
        return StudyWordDto.Recommendation.builder()
                .study(study)
                .candidates(wordCandidates)
                .latestStudyWordIds(latestStudyWordIds)
                .build();

    }
}
