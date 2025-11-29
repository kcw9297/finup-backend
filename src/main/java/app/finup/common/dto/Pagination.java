package app.finup.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {

    // "Page" 객체를 통해 전달받는 값
    private int dataCount;      // 데이터의 총 개수 (같은 조건으로 조회된 총 데이터의 수)
    private int pageNum;        // 현재 페이지
    private int pageSize;       // 한 페이지 당 가져올 행 개수
    private int groupSize;      // 그룹당 페이지 개수

    // 페이징 객체 내부에서 계산하는 페이징 그룹 값
    @JsonProperty("isEndGroup")
    private boolean isEndGroup;     // 다음 그룹 존재 여부
    @JsonProperty("isStartGroup")
    private boolean isStartGroup;   // 이전 그룹 존재 여부
    @JsonProperty("isStartPage")
    private boolean isStartPage;    // 첫 페이지 여부
    @JsonProperty("isEndPage")
    private boolean isEndPage;      // 마지막 페이지 여부

    // 특정 페이지 값 계산
    private int totalPage;              // 총 페이지 수
    private int currentGroup;           // 현재 그룹 번호
    private int prevPage;               // 이전 페이지
    private int nextPage;               // 다음 페이지
    private int nextGroupPage;          // 다음 그룹 시작 페이지
    private int prevGroupPage;          // 이전 그룹 시작 페이지
    private int curGroupStartPage;      // 현재 그룹의 시작 페이지 번호
    private int curGroupEndPage;        // 현재 그룹의 끝 페이지 번호

    private Pagination(int dataCount, int pageNum, int pageSize, int groupSize) {
        this.dataCount = dataCount;
        this.pageNum = pageNum; // Page 객체 생성 시, 이미 +1 하여 계산
        this.pageSize = pageSize;
        this.groupSize = groupSize;
        calculateGroup();
    }

    public static Pagination of(Page<?> page) {
        return new Pagination(page.getDataCount(), page.getPageNum(), page.getPageSize(), page.getGroupSize());
    }

    /**
     * 현재 페이지 기준, 페이지 그룹 계산
     * 페이지와 그룹은 1부터 시작
     */
    public void calculateGroup() {
        // 총 페이지 계산
        totalPage = (int) Math.ceil((double) dataCount / pageSize);
        if (totalPage == 0) totalPage = 1;

        // currentPage는 생성자에서 이미 +1 보정한 상태
        if (pageNum <= 1) pageNum = 1;
        if (pageNum > totalPage) pageNum = totalPage;

        // 현재 그룹 계산 (this.currentPage 사용)
        currentGroup = (int) Math.ceil((double) pageNum / groupSize);

        // 페이지 시작/끝 여부 계산
        isStartPage = pageNum == 1;
        prevPage = isStartPage ? 1 : pageNum - 1;

        isEndPage = pageNum == totalPage;
        nextPage = isEndPage ? pageNum : pageNum + 1;

        // 시작 그룹 여부 및 이전 그룹 페이지 계산
        isStartGroup = currentGroup == 1;
        prevGroupPage = isStartGroup ? 1 : ((currentGroup - 1) * groupSize);

        // 끝 그룹 여부 및 다음 그룹 페이지 계산
        isEndGroup = (currentGroup * groupSize) >= totalPage;
        nextGroupPage = isEndGroup ? totalPage : (currentGroup * groupSize + 1);

        // 현재 그룹에서 시작 및 끝 페이지
        curGroupStartPage = (currentGroup - 1) * groupSize + 1;
        curGroupEndPage = Math.min(currentGroup * groupSize, totalPage);
    }
}
