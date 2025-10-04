/* (C) 2025 */
package pl.dealsniper.core.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    @Value("${app.config.thread-max-pool-size}")
    private Integer maxPoolSize;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(maxPoolSize);
        scheduler.setThreadNamePrefix("deal-sniper-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
