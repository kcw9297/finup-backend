package app.finup.config;

import app.finup.common.utils.LogUtils;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 병렬 처리 설정울 위한 설정 클래스
 * @author kcw
 * @since 2026-01-07
 */

@Slf4j
@Configuration
public class ParallelConfig {

    // 사용 상수
    private static final String PREFIX = "PARALLEL-";
    private static final String PREFIX_CRAWLING = PREFIX + "CRAWLING-";
    private static final String PREFIX_API = PREFIX + "API-";
    private static final String PREFIX_API_NEWS = PREFIX_API + "NEWS-";
    private static final String PREFIX_API_STOCK = PREFIX_API + "STOCK-";
    private static final String PREFIX_API_EMBEDDING = PREFIX_API + "EMBEDDING-";

    // ExecutorService 커스텀 설정

    /*
        ExecutorService 커스텀 설정
            - 동시에 "여러 메소드"를 동시에 실행하는 병렬 처리가 필요한 경우 CompletableFuture 를 사용하는 경우를 위한 설정
            - 스케줄링 로직끼리 비동기 처리를 하는 경우는 @Async 로 처리
     */


    // 웹 크롤링 전용
    @Bean(name = "crawlingExecutor")
    public ExecutorService crawlingExecutor() {

        // 스레드 설정 등록
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                20, // corePoolSize: 항상 유지할 스레드
                40, // maxPoolSize: 최대 스레드
                120L, // keepAliveTime (스레드 최대 생존 시간)
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(150),  // 작업 대기 큐 크기
                new CustomThreadFactory(PREFIX_CRAWLING), // 스레드를 구분할 PREFIX
                new ThreadPoolExecutor.CallerRunsPolicy()  // 작업 대기 큐가 가득 찬 경우 전략 (호출한 스레드가 실행)
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    // 뉴스 API 호출 전용
    @Bean(name = "newsApiExecutor")
    public ExecutorService newsApiExecutor() {

        // 스레드 설정 등록
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5, // corePoolSize: 항상 유지할 스레드
                10, // maxPoolSize: 최대 스레드
                120L, // keepAliveTime (스레드 최대 생존 시간)
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50),  // 작업 대기 큐 크기
                new CustomThreadFactory(PREFIX_API_NEWS), // 스레드를 구분할 PREFIX
                new ThreadPoolExecutor.CallerRunsPolicy()  // 작업 대기 큐가 가득 찬 경우 전략 (호출한 스레드가 실행)
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    // 주식 API 호출 전용
    @Bean(name = "stockApiExecutor")
    public ExecutorService stockApiExecutor() {

        // 스레드 설정 등록
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,  // corePoolSize: 항상 유지할 스레드
                20,  // maxPoolSize: 최대 스레드
                90L, // keepAliveTime (스레드 최대 생존 시간)
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),  // 작업 대기 큐 크기
                new CustomThreadFactory(PREFIX_API_STOCK), // 스레드를 구분할 PREFIX
                new ThreadPoolExecutor.CallerRunsPolicy()  // 작업 대기 큐가 가득 찬 경우 전략 (호출한 스레드가 실행)
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    // Embedding 벡터 생성을 위한 API 호출 전용
    @Bean(name = "embeddingApiExecutor")
    public ExecutorService embeddingApiExecutor() {

        // 스레드 설정 등록
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,  // corePoolSize: 항상 유지할 스레드
                20,  // maxPoolSize: 최대 스레드
                60L, // keepAliveTime (스레드 최대 생존 시간)
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),  // 작업 대기 큐 크기
                new CustomThreadFactory(PREFIX_API_EMBEDDING), // 스레드를 구분할 PREFIX
                new ThreadPoolExecutor.CallerRunsPolicy()  // 작업 대기 큐가 가득 찬 경우 전략 (호출한 스레드가 실행)
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    // ExecutorService 종료 메소드 (애플리케이션 종료 시 처리)
    @PreDestroy
    public void shutdown() {

        LogUtils.showInfo(this.getClass(), "🚨", "ExecutorService 종료 시작");

        // [1] 현재 사용중인 모든 Executor Map 생성 (추후 추가 시 여기에 추가)
        Map<ExecutorService, String> executorPrefixMap = Map.of(
                crawlingExecutor(), PREFIX_CRAWLING,
                newsApiExecutor(), PREFIX_API_NEWS,
                stockApiExecutor(), PREFIX_API_STOCK,
                embeddingApiExecutor(), PREFIX_API_EMBEDDING
        );

        // [2] 일괄 종료 수행
        executorPrefixMap.forEach(this::shutdownExecutorService);
        LogUtils.showInfo(this.getClass(), "✅", "ExecutorService 종료 완료");
    }


    // ExecutorService 종료 처리 (종료 시 현재 스레드가 처리 중인 작업 추적용)
    private void shutdownExecutorService(ExecutorService executor, String name) {

        LogUtils.showInfo(this.getClass(), "🛑", "%s 종료 중", name);

        // 새 작업들은 작업 중단 처리
        executor.shutdown();

        try {
            // 작업 중단까지 60초 대기. 아직 작업이 남은 경유 false
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("[{}] 정상 종료 실패. 강제 종료 시도", name);

                // 아직도 실행 중인 작업 중단 시도
                List<Runnable> notExecuted = executor.shutdownNow();
                log.warn("[{}] 미실행 작업 수: {}", name, notExecuted.size());

                // 작업 중단 30초 추가 대기
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.error("[{}] 강제 종료 실패", name);
                }

                // 정상 종료된 경우
            } else {
                LogUtils.showInfo(this.getClass(), "✅", "%s 정상 종료 완료", name);
            }

            // 인터럽트 발생 시
        } catch (InterruptedException e) {
            log.error("[{}] 종료 중 인터럽트 발생. 강제 종료", name);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    /*
        ThreadFactory 커스터마이징
            - Thread 카운팅 로직
            - Thread Prefix 설정
     */
    private static class CustomThreadFactory implements ThreadFactory {

        // 커스텀 멤버 변수
        private final AtomicInteger counter = new AtomicInteger(0); // 스레드 카운팅 숫자
        private final String prefix;

        CustomThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {

            // 스레드 별 숫자 부여 설정
            Thread thread = new Thread(r);
            thread.setName(prefix + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }
}
