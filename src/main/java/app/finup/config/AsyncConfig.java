package app.finup.config;

import app.finup.common.constant.AsyncMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
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
    private static final String PREFIX_NORMAL = "SCHEDULER-";
    private static final String PREFIX_NEWS = "NEWS-";
    private static final String PREFIX_NEWS_NAVER_API = PREFIX_NEWS + "NAVER_API-";
    private static final String PREFIX_NEWS_CRAWLING = PREFIX_NEWS + "CRAWLER-";
    private static final String PREFIX_STOCK = "STOCK-";
    private static final String PREFIX_STOCK_SEARCH = PREFIX_STOCK + "SEARCH-";


    // 일반 스케줄러
    @Bean(name = AsyncMode.NORMAL)
    public Executor normalScheduler() {
        return new SimpleAsyncTaskExecutor(PREFIX_NORMAL);
    }

    // 뉴스 스케줄러
    @Bean(name = AsyncMode.NEWS)
    public Executor newsScheduler() {
        return new SimpleAsyncTaskExecutor(PREFIX_NEWS_CRAWLING);
    }

    // 주식 스케줄러
    @Bean(name = AsyncMode.STOCK)
    public Executor stockScheduler() {
        return new SimpleAsyncTaskExecutor(PREFIX_STOCK_SEARCH);
    }

}
