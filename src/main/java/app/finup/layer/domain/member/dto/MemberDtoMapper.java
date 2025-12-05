package app.finup.layer.domain.member.dto;


import app.finup.layer.domain.member.entity.Member;

/**
 * Member Entity -> DTO 매퍼 클래스
 * @author khj
 * @since 2025-12-04
 */
public class MemberDtoMapper {
    /**
     * 여러 건 조회: Notice -> Detail DTO
     */

    public static MemberDto.Row toRow(Member entity) {
        return MemberDto.Row.builder()
                .memberId(entity.getMemberId())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .isActive(entity.getIsActive())
                .memberRole(entity.getRole().getValue())
                .socialType(entity.getSocial().getValue())
                .socialId(entity.getSocialId())
                .build();
    }
}
