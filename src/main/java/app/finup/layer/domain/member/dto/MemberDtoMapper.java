package app.finup.layer.domain.member.dto;


import app.finup.layer.domain.member.entity.Member;

/**
 * Member Entity -> DTO 매퍼 클래스
 * @author khj
 * @since 2025-12-04
 */
public class MemberDtoMapper {
    /**
     * 여러 건 조회: member -> Detail DTO
     */

    public static MemberDto.Row toRow(Member entity) {
        return MemberDto.Row.builder()
                .memberId(entity.getMemberId())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .isActive(entity.getIsActive())
                .memberRole(entity.getRole())
                .socialType(entity.getSocial())
                .socialId(entity.getSocialId())
                .profileImageUrl(entity.getProfileImageFile().getFilePath())
                .build();
    }

    public static MemberDto.Join toMemberJoinDto(Member entity) {
        return MemberDto.Join.builder()
                .email(entity.getEmail())
                .build();
    }

    public static MemberDto.Detail toDetail(Member entity) {
        return MemberDto.Detail.builder()
                .memberId(entity.getMemberId())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .profileImageUrl(entity.getProfileImageFile() != null
                        ? entity.getProfileImageFile().getFilePath() : null)
                .build();
    }
}
