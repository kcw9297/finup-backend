package app.finup.layer.domain.notice.dto;

import app.finup.layer.domain.notice.entity.Notice;

/**
 * Notice Entity -> DTO 매퍼 클래스
 * @author khj
 * @since 2025-12-01
 */

public final class NoticeDtoMapper {
    public static NoticeDto.Detail toDto(Notice entity) {
        return NoticeDto.Detail.builder()
                .noticeId(entity.getNoticeId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .cdate(entity.getCdate())
                .udate(entity.getUdate())
                .build();
    }

    public static NoticeDto.NoticeList toNoticeList(Notice entity) {
        return NoticeDto.NoticeList.builder()
                .noticeId(entity.getNoticeId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .cdate(entity.getCdate())
                .udate(entity.getUdate())
                .build();
    }
}
