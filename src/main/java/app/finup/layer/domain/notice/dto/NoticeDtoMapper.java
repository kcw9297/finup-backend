package app.finup.layer.domain.notice.dto;

import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.notice.entity.Notice;

/**
 * Notice Entity -> DTO 매퍼 클래스
 * @author khj
 * @since 2025-12-01
 */

public final class NoticeDtoMapper {

    /**
     * 단건 조회: Notice -> Detail DTO
     */

    public static NoticeDto.Detail toDetailDto(Notice entity) {
        return NoticeDto.Detail.builder()
                .noticeId(entity.getNoticeId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .admin(entity.getAdmin().getNickname())
                .cdate(entity.getCdate())
                .udate(entity.getUdate())
                .build();
    }

    public static NoticeDto.Row toListDto(Notice entity) {
        return NoticeDto.Row.builder()
                .noticeId(entity.getNoticeId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .adminId(entity.getAdmin().getMemberId())
                .cdate(entity.getCdate())
                .udate(entity.getUdate())
                .build();
    }

    /**
     * 작성 요청 DTO → 엔티티 생성
     */
    public static Notice fromWriteDto(NoticeDto.Write dto, Member admin) {
        return Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .admin(admin)
                .build();
    }

    /**
     * 수정 요청 DTO → 엔티티 수정 (엔티티 안에 update 메서드 구현)
     */
    public static void applyEdit(NoticeDto.Edit dto, Notice entity) {
        entity.update(dto.getTitle(), dto.getContent());
    }
}
