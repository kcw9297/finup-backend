package app.finup.layer.domain.notice.dto;

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
                .viewCount(entity.getViewCount())
                .cdate(entity.getCdate())
                .udate(entity.getUdate())
                .build();
    }

    /**
     * 여러 건 조회: Notice -> Detail DTO
     */

    public static NoticeDto.Row toListDto(Notice entity) {
        return NoticeDto.Row.builder()
                .noticeId(entity.getNoticeId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .viewCount(entity.getViewCount())
                .cdate(entity.getCdate())
                .udate(entity.getUdate())
                .build();
    }

}
