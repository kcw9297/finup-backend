package app.finup.layer.base.dto;

import lombok.Data;

import java.util.Objects;

@Data
public abstract class SearchRequest {

    private Integer pageNum;
    private Integer pageSize;

    protected SearchRequest(int pageSize) {
        this.pageNum = 0;
        this.pageSize = pageSize;
    }

    protected SearchRequest() {
        this(5);
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = (Objects.isNull(pageNum) ? 1 : pageNum) - 1;
    }

    public int getOffset() {
        return pageNum * pageSize;
    }

    public int getLimit() {
        return pageSize;
    }
}
