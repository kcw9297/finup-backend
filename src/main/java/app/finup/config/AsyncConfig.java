package app.finup.config;

import app.finup.common.constant.AsyncMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 비동기 스케줄링 설정울 위한 설정 클래스
 * @author kcw
 * @since 2025-12-18
 */

@EnableAsync
@Configuration
public class AsyncConfig {

    // 사용 상수
    private static final String PREFIX = "SCHEDULER-";
    private static final String PREFIX_NEWS = "NEWS-";
    private static final String PREFIX_NEWS_NAVER_API = PREFIX_NEWS + "NAVER_API-";
    private static final String PREFIX_NEWS_CRAWLING = PREFIX_NEWS + "CRAWLER-";
    private static final String PREFIX_STOCK = "STOCK-";
    private static final String PREFIX_STOCK_SEARCH = PREFIX_STOCK + "SEARCH-";


    // 일반 스케줄러 - 초기화 작업용
    @Bean(name = AsyncMode.NORMAL)
    public Executor normalScheduler() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(30);
        executor.setAllowCoreThreadTimeOut(true);  // 유휴 시 코어 스레드도 종료
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix(PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = AsyncMode.NEWS_ASYNC)
    public Executor newsCrawlingScheduler() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(10);
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setThreadNamePrefix(PREFIX_NEWS_CRAWLING);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = AsyncMode.STOCK_ASYNC)
    public Executor stockSearchScheduler() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(10);
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setThreadNamePrefix(PREFIX_STOCK_SEARCH);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
