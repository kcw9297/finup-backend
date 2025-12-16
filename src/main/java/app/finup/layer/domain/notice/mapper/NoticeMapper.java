package app.finup.layer.domain.notice.mapper;

import app.finup.layer.domain.notice.dto.NoticeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NoticeMapper {

    /**
     * 공지 검색
     * @param rq 공지 검색 요청 DTO
     * @return 검색된 페이징 결과 DTO 리스트
     */
    List<NoticeDto.Row> search(NoticeDto.Search rq);


    /**
     * 공지 검색 카운팅 쿼리
     * @param rq 공지 검색 요청 DTO
     * @return 현재 조건 기반 카운팅 된 데이터 개수
     */
    Integer countForSearch(NoticeDto.Search rq);


    /**
     * 조회수 갱신이 필요한 모든 공지 업데이트
     * @param increments 대상 게시글 Map (공지번호 - 조회수 증가 수)
     */
    void updateViewCount(@Param("increments") Map<Long, Long> increments);
}
