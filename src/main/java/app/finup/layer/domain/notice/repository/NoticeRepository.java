package app.finup.layer.domain.notice.repository;

import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
