package app.finup.layer.base.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.UtilsException;
import app.finup.layer.base.inter.Reorderable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * 재졍렬 하기 위한 기능을 제공하는 유틸 클래스
 * @author kcw
 * @since 2025-12-04
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
     * @param <T>             Reorderable 상속 엔티티 클래스
     * @param targetObject    새롭게 재정렬을 시도하는 대상 객체
     * @param objects         재정렬 대상 객체 리스트 (정렬된 리스트)
     * @param reorderPosition 재배치 위치 (시작 : 0)
     * @return 계산된 재정렬 값 (임계점에 도달하거나, 정렬이 불가능한 경우 null)
     */
    public static <T extends Reorderable> Double calculateReorder(T targetObject, List<T> objects, Integer reorderPosition) {

        // [1] 이전, 이후에 존재하는 객체 확인
        PrevNext<T> prevNext = findPrevNext(targetObject, objects, reorderPosition);

        // [2] 존재 여부에 따라 분기하여 계산 후 반환
        return calculateNextOrder(prevNext.prev, prevNext.next);
    }

    // 정렬 대상 기준, 앞/뒤에 있는 객체 존재 여부 판단
    private static <T> PrevNext<T> findPrevNext(T targetObject, List<T> objects, Integer reorderPosition) {

        // [1] 이동할 객체를 제외한 리스트 생성
        List<T> objectsWithoutTarget = objects.stream()
                .filter(obj -> !Objects.equals(obj, targetObject))
                .toList();

        // [2] 이전, 이후에 존재하는 객체 확인 후 존재 시 삽입
        T prev = null;
        T next = null;

        // 맨 앞(0)으로 이동
        if (reorderPosition == 0) {
            next = objectsWithoutTarget.isEmpty() ? null : objectsWithoutTarget.get(0);
        }
        // 맨 뒤로 이동
        else if (reorderPosition >= objects.size() - 1) {
            prev = objectsWithoutTarget.isEmpty() ? null : objectsWithoutTarget.get(objectsWithoutTarget.size() - 1);
        }
        // 중간으로 이동
        else {
            prev = objectsWithoutTarget.get(reorderPosition - 1);
            next = objectsWithoutTarget.get(reorderPosition);
        }

        // [3] 결과 반환
        return new PrevNext<>(prev, next);
    }

    // 다음 정렬 값 계산
    private static <T extends Reorderable> Double calculateNextOrder(T prev, T next) {

        // 두 객체 사이로 보내는 경우
        if (Objects.nonNull(prev) && Objects.nonNull(next)) {

            // 새로운 정렬 값 계산
            double newDisplayOrder = (prev.getDisplayOrder() + next.getDisplayOrder()) / 2;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null
            return newDisplayOrder < REBALANCE_THRESHOLD_MIN || newDisplayOrder > REBALANCE_THRESHOLD_MAX ? null : newDisplayOrder;

            // 맨 뒤로 보내는 경우
        } else if (Objects.nonNull(prev)) {

            // 새로운 정렬 값 계산
            double newDisplayOrder = prev.getDisplayOrder() + DISPLAY_ORDER_INCREMENT;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null
            return newDisplayOrder > REBALANCE_THRESHOLD_MAX ? null : newDisplayOrder;

            // 맨 앞으로 보내는 경우
        } else if (Objects.nonNull(next)) {

            // 새로운 정렬 값 계산
            double newDisplayOrder = next.getDisplayOrder() - DISPLAY_ORDER_INCREMENT;

            // 만약 임계점을 초과하면 재정렬 요청을 위해 null
            return newDisplayOrder < REBALANCE_THRESHOLD_MIN ? null : newDisplayOrder;

            // 둘 다 없는 경우, 정렬을 할 수 없는 상태 (잘못된 요청이거나, 자신만 존재하는 경우)
        } else {
            throw new UtilsException(AppStatus.UTILS_REORDER_FAILED);
        }
    }


    /**
     * 일괄 재정렬 (임계치에 도달한 경우)
     * @param <T>             Reorderable 상속 엔티티 클래스
     * @param targetObject    새롭게 재정렬을 시도하는 대상 객체
     * @param objects         재정렬 대상 객체 리스트 (정렬된 리스트)
     * @param reorderPosition 재배치 위치 (시작 : 0)
     * @return 계산된 재정렬 값
     */
    public static <T extends Reorderable> Double rebalanceAndReorder(T targetObject, List<T> objects, Integer reorderPosition) {

        // [1] 일괄 재정렬
        for (int i = 0; i < objects.size(); i++)
            objects.get(i).reorder(DEFAULT_DISPLAY_ORDER + (i * DISPLAY_ORDER_INCREMENT));

        // [2] 재시도 수행
        Double displayOrder = calculateReorder(targetObject, objects, reorderPosition);

        // 만약 정렬 시도 후에도 null 인 경우, 정렬 로직에 문제가 있으므로 예외 던짐
        if (Objects.isNull(displayOrder)) throw new UtilsException(AppStatus.UTILS_REORDER_FAILED);

        // [3] 정렬 값 반환
        return displayOrder;
    }


    // 내부 Record
    private record PrevNext<T>(T prev, T next) {}
}
