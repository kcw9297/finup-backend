package app.finup.common.dto;

import lombok.Data;

import java.util.List;


/**
 * 서비스의 페이징 결과를 반환하기 위한 DTO
 * @param <T> 페이징 데이터 타입
 * @author kcw
 * @since 2025-11-26
 */
@Data
public class Page<T> {

    private List<T> rows;       // 페이징 결과 데이터 리스트
    private int dataCount;      // 데이터의 총 개수 (같은 조건으로 조회된 총 데이터의 수)
    private int pageNum;        // 현재 페이지
    private int pageSize;       // 한 페이지 당 가져올 행 개수
    private int groupSize;      // 그룹당 페이지 개수

    private Page(List<T> rows, int dataCount, int pageNum, int pageSize, int groupSize) {
        this.rows = rows;
        this.dataCount = dataCount;
        this.pageNum = pageNum + 1;
        this.pageSize = pageSize;
        this.groupSize = groupSize;
    }

    /**
     * 페이징 데이터를 담기 위한 객체 생성
     * @param rows 페이징 데이터 리스트
     * @param dataCount 데이터 총 개수
     * @param pageNum 현재 페이지 번호
     * @param pageSize 페이징 크기
     * @return 생성된 페이징 객체
     * @param <T> 페이징 데이터 타입
     */
    public static <T> Page<T> of(List<T> rows, int dataCount, int pageNum, int pageSize) {
        return new Page<>(rows, dataCount, pageNum, pageSize, 5);
    }

    /**
     * 페이징 데이터를 담기 위한 객체 생성 (그룹 사이즈 설정)
     * @param rows 페이징 데이터 리스트
     * @param dataCount 데이터 총 개수
     * @param pageNum 현재 페이지 번호
     * @param pageSize 페이징 크기
     * @param groupSize 페이징 그룹 크기
     * @return 생성된 페이징 객체
     * @param <T> 페이징 데이터 타입
     */
    public static <T> Page<T> of(List<T> rows, int dataCount, int pageNum, int pageSize, int groupSize) {
        return new Page<>(rows, dataCount, pageNum, pageSize, groupSize);
    }

}
