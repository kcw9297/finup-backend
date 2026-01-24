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
        executor.setThreadNamePrefix(PREFIX);
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(10);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(30);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAwaitTerminationSeconds(10);  // 종료 대기 시간
        executor.setWaitForTasksToCompleteOnShutdown(true);  // 안전한 종료
        executor.initialize();
        return executor;
    }

    @Bean(name = AsyncMode.NEWS)
    public Executor newsCrawlingScheduler() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(PREFIX_NEWS_CRAWLING);
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setAwaitTerminationSeconds(60);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = AsyncMode.STOCK)
    public Executor stockSearchScheduler() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(PREFIX_STOCK_SEARCH);
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(20);
        executor.setKeepAliveSeconds(60);
        executor.setAwaitTerminationSeconds(60);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
