package app.finup.layer.base.utils;

import app.finup.layer.base.inter.Reorderable;

import java.util.List;
import java.util.Objects;

/**
 * 재졍렬 하기 위한 기능을 제공하는 유틸 클래스
 * @author kcw
 * @since 2025-12-04
 */
public class ReorderUtils {

    // 사용 상수
    public static final Double DEFAULT_DISPLAY_ORDER = 1000.0; // 최초 삽입 시, 기본 정렬 값
    public static final Double DISPLAY_ORDER_INCREMENT = 100.0; // 삽입 시 기본 증가량
    private static final Double REBALANCE_THRESHOLD_MIN = 1.0; // 재졍렬이 필요한 임계 값 기준
    private static final Double REBALANCE_THRESHOLD_MAX = 10000000.0; // 재졍렬이 필요한 임계 값 기준


    /**
     * 가장 마지막 정렬 값 계산
     * @param target 가장 마지막에 위치한 객체
     * @return 계산된 다음 정렬 값
     * @param <T> Reorderable 상속 엔티티 클래스
     */
    public static <T extends Reorderable> Double calculateNextOrder(T target) {
        return target.getDisplayOrder() + DISPLAY_ORDER_INCREMENT;
    }


    /**
     * 재정렬 위치 값 계산
     * @param target 정렬 대상
     * @param prev 정렬 대상보다 앞에 있는 객체
     * @param next 정렬 대상보다 뒤에 있는 객체
     * @param <T> Reorderable 상속 엔티티 클래스
     * @return 계산된 재정렬 값
     */
    public static <T extends Reorderable> Double calculateReorder(T target, T prev, T next) {

        // [1] 이전/이후 값 존재 확인
        boolean isPrevExists = Objects.nonNull(prev);
        boolean isNextExists = Objects.nonNull(next);
        double newDisplayOrder;

        // [2] 존재 여부에 따라 분기하여 계산
        // 사이에 이동하는 경우
        if (isPrevExists && isNextExists) {

            // 새로운 정렬 값 계산
            newDisplayOrder = (prev.getDisplayOrder() + next.getDisplayOrder()) / 2;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null 반환
            if (newDisplayOrder < REBALANCE_THRESHOLD_MIN) return null;

            // 맨 뒤로 보내는 경우
        } else if (isPrevExists) {

            // 새로운 정렬 값 계산
            newDisplayOrder = prev.getDisplayOrder() + DISPLAY_ORDER_INCREMENT;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null 반환
            if (newDisplayOrder > REBALANCE_THRESHOLD_MAX) return null;

            // 맨 앞으로 보내는 경우
        } else if (isNextExists) {

            // 새로운 정렬 값 계산
            newDisplayOrder = next.getDisplayOrder() - DISPLAY_ORDER_INCREMENT;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null 반환
            if (newDisplayOrder < REBALANCE_THRESHOLD_MIN) return null;

            // 둘 다 없는 경우, 정렬을 할 수 없는 상태 (제자리)
        } else {
            newDisplayOrder = target.getDisplayOrder();
        }

        // [3] 계산된 displayOrder 반환
        return newDisplayOrder;
    }


    /**
     * 일괄 재정렬 (임계치에 도달한 경우)
     * @param objects 재정렬 대상 객체 리스트 (정렬된 리스트)
     * @param <T> Reorderable 상속 엔티티 클래스
     */
    public static <T extends Reorderable> void rebalance(List<T> objects) {

        // 이미 외부에서 정렬된 리스트를 받아 재정렬
        for (int i = 0; i < objects.size(); i++)
            objects.get(i).reorder(DEFAULT_DISPLAY_ORDER + (i * DISPLAY_ORDER_INCREMENT));
    }
}
