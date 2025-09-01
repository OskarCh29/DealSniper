/* (C) 2025 */
package pl.dealsniper.core.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    private static final Integer MAX_POOL_SIZE = 20;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(MAX_POOL_SIZE);
        scheduler.setThreadNamePrefix("deal-sniper-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
