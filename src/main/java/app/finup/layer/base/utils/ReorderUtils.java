package app.finup.layer.base.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.UtilsException;
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
     * @param objects 재정렬 대상 객체 리스트 (정렬된 리스트)
     * @param reorderPosition 재배치 위치 (시작 : 0)
     * @param <T> Reorderable 상속 엔티티 클래스
     * @return 계산된 재정렬 값 (임계점에 도달하거나, 정렬이 불가능한 경우 null)
     */
    public static <T extends Reorderable> Double calculateReorder(List<T> objects, Integer reorderPosition) {

        // [1] 이전, 이후에 존재하는 객체 확인
        T prev = reorderPosition <= 0 ? null : objects.get(reorderPosition - 1);
        T next = reorderPosition == objects.size() - 1 ? null : objects.get(reorderPosition + 1);

        // [2] 존재 여부에 따라 분기하여 계산
        Double newDisplayOrder;

        // 두 객체 사이로 보내는 경우
        if (Objects.nonNull(prev) && Objects.nonNull(next)) {

            // 새로운 정렬 값 계산
            newDisplayOrder = (prev.getDisplayOrder() + next.getDisplayOrder()) / 2;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null
            if (newDisplayOrder < REBALANCE_THRESHOLD_MIN) newDisplayOrder = null;

            // 맨 뒤로 보내는 경우
        } else if (Objects.nonNull(prev)) {

            // 새로운 정렬 값 계산
            newDisplayOrder = prev.getDisplayOrder() + DISPLAY_ORDER_INCREMENT;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null
            if (newDisplayOrder > REBALANCE_THRESHOLD_MAX) newDisplayOrder = null;

            // 맨 앞으로 보내는 경우
        } else if (Objects.nonNull(next)) {

            // 새로운 정렬 값 계산
            newDisplayOrder = next.getDisplayOrder() - DISPLAY_ORDER_INCREMENT;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null
            if (newDisplayOrder < REBALANCE_THRESHOLD_MIN) newDisplayOrder = null;

            // 둘 다 없는 경우, 정렬을 할 수 없는 상태 (잘못된 요청이거나, 자신만 존재하는 경우)
        } else {
            throw new UtilsException(AppStatus.UTILS_REORDER_FAILED);
        }

        // [3] 계산된 displayOrder 반환
        return newDisplayOrder;
    }



    /**
     * 일괄 재정렬 (임계치에 도달한 경우)
     * @param objects 재정렬 대상 객체 리스트 (정렬된 리스트)
     * @param reorderPosition 재배치 위치 (시작 : 0)
     * @param <T> Reorderable 상속 엔티티 클래스
     * @return 계산된 재정렬 값
     */
    public static <T extends Reorderable> Double rebalanceAndReorder(List<T> objects, Integer reorderPosition) {

        // [1] 일괄 재정렬
        for (int i = 0; i < objects.size(); i++)
            objects.get(i).reorder(DEFAULT_DISPLAY_ORDER + (i * DISPLAY_ORDER_INCREMENT));

        // [2] 재시도 수행
        Double displayOrder = calculateReorder(objects, reorderPosition);

        // 만약 정렬 시도 후에도 null 인 경우, 정렬 로직에 문제가 있으므로 예외 던짐
        if (Objects.isNull(displayOrder)) throw new UtilsException(AppStatus.UTILS_REORDER_FAILED);

        // [3] 정렬 값 반환
        return displayOrder;
    }
}
