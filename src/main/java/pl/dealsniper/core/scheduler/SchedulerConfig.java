/* (C) 2025 */
package pl.dealsniper.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pl.dealsniper.core.configuration.ApplicationConfiguration;

@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {

    private final ApplicationConfiguration properties;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(properties.getConfig().getThreadMaxPoolSize());
        scheduler.setThreadNamePrefix("deal-sniper-scheduler-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }
}
