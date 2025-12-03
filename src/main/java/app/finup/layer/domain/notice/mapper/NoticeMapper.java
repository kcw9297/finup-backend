package app.finup.layer.domain.notice.mapper;

import app.finup.layer.domain.notice.dto.NoticeDto;

import java.util.List;

public interface NoticeMapper {
    List<NoticeDto.Row> search(NoticeDto.Search rq);
    Long countForSearch(NoticeDto.Search rq);
}
