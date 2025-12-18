package app.finup.layer.domain.videolink.mapper;

import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 학습 영상 동적 쿼리를 위한 MyBatis Mapper 인터페이스
 * @author kcw
 * @since 2025-12-10
 */
@Mapper
@Repository
public interface VideoLinkMapper {

    /**
     * 학습 영상 검색
     * @param rq 검색 요청 DTO
     * @return 페이징된 단계 학습 단어정보 DTO 리스트
     */
    List<VideoLinkDto.Row> search(VideoLinkDto.Search rq);


    /**
     * 학습 영상 검색 카운트 쿼리
     * @param rq 검색 요청 DTO
     * @return 검색 파라미터를 적용한 전체 데이터 수
     */
    Integer countForSearch(VideoLinkDto.Search rq);

    /**
     * 홈 최신 영상
     * @param limit 갯수 제한
     * @return 홈에 보여질 영상 데이터 수
     */
    List<VideoLinkDto.Row> selectHomeLatest(@Param("limit") Integer limit);
}
