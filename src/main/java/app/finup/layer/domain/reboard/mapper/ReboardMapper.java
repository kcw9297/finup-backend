package app.finup.layer.domain.reboard.mapper;

import app.finup.layer.domain.reboard.dto.ReboardDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ReboardMapper {

    List<ReboardDto.Row> search(ReboardDto.Search rq);

    Integer searchCount(ReboardDto.Search rq);
}
