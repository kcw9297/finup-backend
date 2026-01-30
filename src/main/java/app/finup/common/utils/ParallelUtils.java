package app.finup.common.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.AppException;
import app.finup.common.exception.UtilsException;
import app.finup.common.support.ThreadAwait;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 멀티스레드 병렬 처리 기능을 제공하는 클래스
 * @author kcw
 * @since 2025-12-30
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParallelUtils {

    // SEMAPHORE (최대 작업 스레드 제한)
    public static final Semaphore SEMAPHORE_UNLIMITED = new Semaphore(Integer.MAX_VALUE);
    public static final Semaphore SEMAPHORE_NEWS_CRAWLING_NAVER = new Semaphore(3);
    public static final Semaphore SEMAPHORE_NEWS_CRAWLING_ETC = new Semaphore(10);
    public static final Semaphore SEMAPHORE_SYNC_STOCK = new Semaphore(5);

    // 최대 재시도 횟수 (지금은 그냥 여기서 일괄 통제)
    private static final int MAX_RETRY = 5;
    private static final Duration MIN_429_RETRY_WAIT = Duration.ofSeconds(1); // 429 재시도 시 최소 대기시간
    private static final Duration MAX_429_RETRY_WAIT = Duration.ofSeconds(10); // 429 재시도 시 최소 대기시간
    private static final Duration RETRY_WAIT_ALL_JITTER = Duration.ofMillis(500); // 일괄 대기시간에 부여하는 JITTER
    private static final Duration MAX_RETRY_WAIT_ALL = Duration.ofSeconds(60); // 일괄 대기 최대 대기시간


    /**
     * 쓰레드 대기 수행
     * @param wait 대기 시간
     * @param clazz 대기 요청 클래스
     */
    public static void wait(Duration wait, Class<?> clazz) {
        try {
            TimeUnit.MILLISECONDS.sleep(wait.toMillis());
        }  catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogUtils.showWarn(clazz, "인터럽트 발생으로 대기 처리 실패.");
        }
    }


    /**
     * 병렬 작업 수행 (입력, 반환 값 없음)
     * @param workName        수행 작업명
     * @param tasks           처리 작업 목록
     * @param executorService 병렬 처리를 제어할 ExecutorService
     */
    public static void doParallelRun(
            String workName,
            List<Runnable> tasks,
            Semaphore semaphore,
            ExecutorService executorService) {

        // Consumer를 Function으로 변환 (반환값 Void)
        doParallel(workName, tasks, ParallelUtils::doRun, semaphore, executorService, Duration.ZERO);
    }


    // runnable 메소드 처리
    private static Objects doRun(Runnable task) {
        task.run();
        return null;
    }


    /**
     * 병렬 작업 수행 (입력, 반환 값 존재)
     * @param <T>             입력 타입
     * @param <R>             반환 타입
     * @param workName        수행 작업명
     * @param items           처리할 아이템 리스트
     * @param mappingTask     각 아이템에 적용할 함수
     * @param semaphore       최대 동시 실행 제한
     * @param executorService 병렬 처리를 제어할 ExecutorService
     * @return 모든 작업의 결과 리스트 (순서 보장 X)
     */
    public static <T, R> List<R> doParallelTask(
            String workName,
            Collection<T> items,
            Function<T, R> mappingTask,
            Semaphore semaphore,
            ExecutorService executorService) {

        return doParallel(workName, items, mappingTask, semaphore, executorService, Duration.ZERO);
    }


    /**
     * 병렬 작업 수행 (입력, 반환 값 존재)
     * @param <T>             입력 타입
     * @param <R>             반환 타입
     * @param workName        수행 작업명
     * @param items           처리할 아이템 리스트
     * @param mappingTask     각 아이템에 적용할 함수
     * @param semaphore       최대 동시 실행 제한
     * @param executorService 병렬 처리를 제어할 ExecutorService
     * @param delay           각 작업 완료 후 부여할 딜레이
     * @return 모든 작업의 결과 리스트 (순서 보장 X)
     */
    public static <T, R> List<R> doParallelTask(
            String workName,
            Collection<T> items,
            Function<T, R> mappingTask,
            Semaphore semaphore,
            ExecutorService executorService,
            Duration delay) {

        return doParallel(workName, items, mappingTask, semaphore, executorService, delay);
    }


    /**
     * 병렬 작업 수행 (반환 값 없음, 입력 값 존재)
     *
     * @param <T>             입력 타입
     * @param workName        수행 작업명
     * @param items           처리할 아이템 리스트
     * @param task            각 아이템에 적용할 함수
     * @param semaphore       최대 동시 실행 제한
     * @param executorService 병렬 처리를 제어할 ExecutorService
     */
    public static <T> void doParallelConsume(
            String workName,
            Collection<T> items,
            Consumer<T> task,
            Semaphore semaphore,
            ExecutorService executorService) {

        // Consumer를 Function으로 변환 (반환값 Void)
        doParallel(workName, items, item -> doAccept(task, item), semaphore, executorService, Duration.ZERO);
    }


    /**
     * 병렬 작업 수행 (반환 값 없음, 입력 값 존재)
     * @param <T>             입력 타입
     * @param workName        수행 작업명
     * @param items           처리할 아이템 리스트
     * @param task            각 아이템에 적용할 함수
     * @param semaphore       최대 동시 실행 제한
     * @param executorService 병렬 처리를 제어할 ExecutorService
     * @param delay           각 작업 완료 후 부여할 딜레이
     */
    public static <T> void doParallelConsume(
            String workName,
            Collection<T> items,
            Consumer<T> task,
            Semaphore semaphore,
            ExecutorService executorService,
            Duration delay) {

        // Consumer를 Function으로 변환 (반환값 Void)
        doParallel(workName, items, item -> doAccept(task, item), semaphore, executorService, delay);
    }


    // Consumer 처리 메소드 (doParallel은 Function 사용하므로, 반환 값은 버림)
    private static <T> Object doAccept(Consumer<T> task, T item) {
        task.accept(item);
        return null;
    }


    // WorkQueue 기반 병렬 처리 로직 수행
    private static <T, R> List<R> doParallel(
            String workName,
            Collection<T> items,
            Function<T, R> mappingTask,
            Semaphore semaphore,
            ExecutorService executorService,
            Duration delay) {

        // 유효하지 않은 요청은 작업 미수행
        if (Objects.isNull(items) || items.isEmpty()) return List.of();

        // [1] 작업 수행 및 수행 결과를 저장할 Queue 생성
        Map<T, AtomicInteger> asyncItems = items.stream().collect(Collectors.toConcurrentMap(Function.identity(), item -> new AtomicInteger(0)));
        BlockingQueue<T> workQueue = new LinkedBlockingQueue<>(items);
        ConcurrentLinkedQueue<R> results = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<T> failedItems = new ConcurrentLinkedQueue<>(); // 작업에 실패한 item
        int maxWorkers = Math.min(items.size(), semaphore.availablePermits()); // 작업 최대 Worker 수

        // [2] 시작 로그 및 시작시간 측정
        log.info("""
        
        ┌─ PARALLEL WORK START ────────────────────────────────────────────────────────────
        │ 수행 작업명\t\t\t{}
        │ 총 작업 수\t\t\t{}
        │ Worker 수\t\t\t{}
        │ 시작 시간\t\t\t{}
        └───────────────────────────────────────────────────────────────────────────────
        """, workName, items.size(), maxWorkers, TimeUtils.formatDateTime(TimeUtils.getNowLocalDateTime()));

        // [3] 작업 시작 전 필요 값 정의
        long startTime = System.currentTimeMillis(); // 시작 시간
        AtomicInteger totalProcessed = new AtomicInteger(0); // 모든 스레드가 진행한 프로세스 수
        AtomicInteger totalFailed = new AtomicInteger(0);  //  모든 스레드 진행 프로세스 중, 최종적으로 실패한 작업 수

        // [3] 워커 실행
        List<CompletableFuture<Void>> workers =
                runWorker(asyncItems, mappingTask, semaphore, executorService, maxWorkers, workQueue, results, totalProcessed, failedItems, totalFailed, delay);

        // [4] 모든 작업 완료 처리
        try {
            // 작업 완료 대기
            CompletableFuture.allOf(workers.toArray(new CompletableFuture[0])).get(90, TimeUnit.MINUTES);

            // 작업 결과 로그 출력
            log.info("""
            
            ┌─ PARALLEL WORK COMPLETED  ───────────────────────────────────────────────────────
            │ 수행 작업명\t\t\t\t{}
            │ 총 처리 작업 수\t\t\t{}/{}
            │ 성공/실패 작업 수\t\t성공: {}\t\t실패: {}
            │ 소요 시간\t\t\t\t{}
            │ 종료 시간\t\t\t\t{}
            └───────────────────────────────────────────────────────────────────────────────
            """, workName, totalProcessed.get(), items.size(), totalProcessed.get(), failedItems.size(),
                    LogUtils.calculateCost(startTime), TimeUtils.formatDateTime(TimeUtils.getNowLocalDateTime()));


        } catch (TimeoutException e) {
            log.error("❌ Work Queue 타임아웃 - 처리: {}/{}", totalProcessed.get(), items.size());
            workers.forEach(worker -> worker.cancel(true));

        } catch (Exception e) {
            log.error("❌ Work Queue 실행 오류: {}", e.getMessage());
        }

        return new ArrayList<>(results);
    }


    private static <T, R> List<CompletableFuture<Void>> runWorker(
            Map<T, AtomicInteger> asyncItems,
            Function<T, R> mappingTask,
            Semaphore semaphore,
            ExecutorService executorService,
            int maxWorkers,
            BlockingQueue<T> workQueue,
            ConcurrentLinkedQueue<R> results,
            AtomicInteger totalProcessed,
            ConcurrentLinkedQueue<T> failedItems,
            AtomicInteger totalFailed,
            Duration delay) {

        // 쓰레드 일괄 대기를 지원할 객체
        ThreadAwait threadAwait = new ThreadAwait();

        // 병렬 작업 수행
        return IntStream.rangeClosed(1, maxWorkers)
                .mapToObj(workerIndex -> CompletableFuture.runAsync(() -> {

                    // 현재 설정된 딜레이 값을 밀리초로 변환
                    long baseDelay = delay.toMillis();

                    // 초기 딜레이 수행
                    try {
                        delay(baseDelay * (workerIndex - 1), false);
                    } catch (InterruptedException e) {
                        log.error("❌ WORKER-{}\t 초기 실행 인터럽트 발생. 종료: {}", workerIndex, e.getMessage());
                        Thread.currentThread().interrupt();
                        return;
                    }

                    // 시작 안내
                    log.info("⚙️ WORKER-{}\t 작업 시작 (남은 작업: {})", workerIndex, workQueue.size());
                    int successCount = 0;  // 성공 작업 카운트
                    int failedCount = 0;   // 실패 작업 카운트
                    int processedCount = 0; // 성공/실패를 떠나 현재 스레드가 담당했던 작업 카운트 (재시도 포함)

                    // 스레드가 Worker Queue 에서 작업을 꺼내 수행
                    while (true) {

                        try {

                            // 만약 현재 대기시간이 존재하면 대기
                            threadAwait.awaitIfPaused(MAX_RETRY_WAIT_ALL, RETRY_WAIT_ALL_JITTER);

                            // 1초의 Timeout을 가지고, 작업 큐에서 수행할 작업 대상을 꺼내옴
                            T item = workQueue.poll(1, TimeUnit.SECONDS);

                            // 작업을 얻어오는데 실패한 경우, 큐 상태 확인
                            if (Objects.isNull(item)) {
                                if (workQueue.isEmpty()) break; // 큐가 비어있는 경우 작업 중단
                                continue; // 작업이 남아있고, 일시적인 null이면 재시도 수행
                            }

                            // 로직 수행
                            try {

                                // 작업 수행 후, 결과 반환 (Semaphore 제한 적용)
                                R result = runWithLimit(semaphore, () -> mappingTask.apply(item));

                                // 작업 처리 queue에 결과 데이터 삽입
                                if (Objects.nonNull(result)) results.add(result); // null 방지
                                totalProcessed.incrementAndGet(); // 처리한 총 프로세스 수 증가
                                successCount++;

                                // 작업 수행 후 전체 대기시간 제거
                                threadAwait.clear();

                                // 로직 실패 재시도 처리
                            } catch (Exception e) {

                                // 실패 시 현재 item의 재시도 횟수 검증
                                AtomicInteger retryCounter = asyncItems.get(item);
                                int attemptNumber = retryCounter.incrementAndGet();

                                // 최대 횟수를 넘기지 않은 경우, 다시 큐에 삽입
                                if (attemptNumber <= MAX_RETRY) {

                                    // 다시 큐에 삽입 (재시도)
                                    workQueue.put(item);
                                    log.warn("⚠️ WORKER-{}\t 작업 실패 ({}차 시도) - 재시도 예정. 원인 : {}", workerIndex, attemptNumber, e.getMessage());

                                    // 대기시간 부여
                                    if (e instanceof AppException ae && Objects.equals(ae.getAppStatus(), AppStatus.TOO_MANY_REQUEST))
                                        threadAwait.setWaitTimeMillis(calculateRetry429(attemptNumber));

                                    // 만약 최대 횟수를 넘어간 경우, 큐에 다시 삽입하지 않고 실패 처리
                                } else {
                                    failedItems.add(item);
                                    asyncItems.remove(item);
                                    totalFailed.incrementAndGet();
                                    failedCount++;
                                    String message = Objects.isNull(e.getMessage()) ? e.getClass().getSimpleName() : e.getMessage();
                                    log.error("❌ WORKER-{}\t 최종 실패 ({}차 시도 모두 실패): {}", workerIndex, attemptNumber - 1, message);
                                }

                                // 현재 스레드가 담당한 작업 카운트 증가
                            } finally {
                                // 작업 카운터 증가
                                processedCount++;

                                // 대기시간 적용 (항상 수행하는 대기)
                                delay(baseDelay, true);
                            }

                        } catch (InterruptedException e) {
                            log.error("❌ WORKER-{}\t 인터럽트 발생. 종료", workerIndex);
                            Thread.currentThread().interrupt();
                            break;

                        } catch (Exception e) {
                            log.error("❌ WORKER-{}\t 기타 사유로 작업 처리 실패: {}", workerIndex, e.getMessage());
                            break;
                        }
                    }

                    // 작업 완료 로그
                    //log.info("✅ WORKER-{}\t 작업 완료 - 성공: {}\t 실패: {}\t 처리작업수: {}",
                    //        workerIndex, successCount, failedCount, processedCount);

                }, executorService))
                .toList();
    }


    // delay 수행 함수
    private static void delay(long delay, boolean setJitter) throws InterruptedException {

        if (delay > 0) {
            long jitter = setJitter ? ThreadLocalRandom.current().nextLong(-delay / 4, delay / 4 + 1) : 0L; // 0.25 jitter
            TimeUnit.MILLISECONDS.sleep(delay + jitter);
        }
    }


    // 429 Error 발생 시, 재시도 딜레이 계산
    private static long calculateRetry429(int attempt) {
        long backoff = MIN_429_RETRY_WAIT.toMillis() * (1L << (attempt - 1)); // 지수적 상승
        return Math.min(backoff, MAX_429_RETRY_WAIT.toMillis());
    }


    // 동시 실행 스레드를 제한한 작업 수행
    private static <T> T runWithLimit(Semaphore semaphore, Supplier<T> task) throws InterruptedException {

        try {
            // 30초 동안 작업 획득 대기 (최대 프로세스 제한)
            if (!semaphore.tryAcquire(30, TimeUnit.SECONDS))
                throw new UtilsException(AppStatus.UTILS_SEMAPHORE_ACQUIRE_FAILED);

            // 로직 수행
            return task.get();

        } finally {
            // semaphore 사용 후 반환 필수
            semaphore.release();
        }
    }
}
