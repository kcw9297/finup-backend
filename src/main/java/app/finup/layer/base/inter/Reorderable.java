package app.finup.layer.base.inter;

/**
 * 유동적인 재정렬이 가능한 클래스를 표기하기 위한 인터페이스
 * @author kcw
 * @since 2025-12-04
 */
public interface Reorderable {

    /**
     * 현재 정렬 값 조회
     * @return 현재 정렬 값
     */
    Double getDisplayOrder();


    /**
     * 정렬 값 변경 (재정렬)
     * @param displayOrder 변경할 정렬 값
     */
    void reorder(Double displayOrder);
}
