package app.finup.layer.domain.notice.repository;

import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> search(NoticeDto.Search rq);
    Long searchCount(NoticeDto.Search rq);
}
