package app.finup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 스케줄링 설정울 위한 설정 클래스
 * @author kcw
 * @since 2025-12-18
 */

@EnableAsync
@Configuration
public class AsyncConfig {

    // 일반 스케줄러
    @Bean(name = "schedulerExecutor")
    public Executor schedulerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);          // 동시 실행 가능한 기본 스레드 수
        executor.setMaxPoolSize(10);          // 최대 스레드 수
        executor.setQueueCapacity(100);       // 대기 큐 크기
        executor.setThreadNamePrefix("scheduler-");
        executor.initialize();
        return executor;
    }

    /**
     * 퀴즈 생성 전용 ThreadPool (무거운 작업)
     */
    @Bean(name = "quizExecutor")
    public Executor quizExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("quiz-");
        executor.initialize();
        return executor;
    }

    /**
     * 뉴스 수집 전용 ThreadPool
     */
    @Bean(name = "newsExecutor")
    public Executor newsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("news-");
        executor.initialize();
        return executor;
    }

    /**
     * 주식 데이터 수집 전용 ThreadPool
     */
    @Bean(name = "stockExecutor")
    public Executor stockExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("stock-");
        executor.initialize();
        return executor;
    }
}
