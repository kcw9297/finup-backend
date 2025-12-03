package app.finup.layer.domain.study.mapper;

import app.finup.layer.domain.study.dto.StudyDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 단계 학습 동적 쿼리를 위한 MyBatis Mapper 인터페이스
 * @author kcw
 * @since 2025-12-03
 */
@Mapper
@Repository
public interface StudyMapper {

    /**
     * 단계 학습 검색 (현재는 무한 스크롤을 위한 정렬/페이징만 수행)
     * @param rq 검색 요청 DTO
     * @return 페이징된 단계 학습정보 DTO 리스트
     */
    List<StudyDto.Row> search(StudyDto.Search rq);


    /**
     * 단계 학습 검색 카운트 쿼리
     * @param rq 검색 요청 DTO
     * @return 검색 파라미터를 적용한 전체 데이터 수
     */
    Integer countForSearch(StudyDto.Search rq);
}
