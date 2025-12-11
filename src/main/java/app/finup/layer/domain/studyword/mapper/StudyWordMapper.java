package app.finup.layer.domain.studyword.mapper;

import app.finup.layer.domain.studyword.dto.StudyWordDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 단계 학습 단어 동적 쿼리를 위한 MyBatis Mapper 인터페이스
 * @author kcw
 * @since 2025-12-10
 */
@Mapper
@Repository
public interface StudyWordMapper {

    /**
     * 단계 학습 단어 검색
     * @param rq 검색 요청 DTO
     * @return 페이징된 단계 학습 단어정보 DTO 리스트
     */
    List<StudyWordDto.Row> search(StudyWordDto.Search rq);


    /**
     * 단계 학습 단어 검색 카운트 쿼리
     * @param rq 검색 요청 DTO
     * @return 검색 파라미터를 적용한 전체 데이터 수
     */
    Integer countForSearch(StudyWordDto.Search rq);
}
