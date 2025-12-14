package app.finup.layer.domain.memberWordbook.dto;

import app.finup.layer.domain.memberWordbook.entity.MemberWordbook;
import app.finup.layer.domain.studyprogress.dto.StudyProgressDto;
import app.finup.layer.domain.studyprogress.entity.StudyProgress;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 내 단어장 Entity -> DTO 매핑 지원 클래스
 * @author khj
 * @since 2025-12-14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberWordbookDtoMapper {

    public static MemberWordbookDto.Row toRow(MemberWordbook entity) {
        return MemberWordbookDto.Row.builder()
                .termId(entity.getWord().getTermId())
                .name(entity.getWord().getName())
                .description(entity.getWord().getDescription())
                .build();
    }
}
