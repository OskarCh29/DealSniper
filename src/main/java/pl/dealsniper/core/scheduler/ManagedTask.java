/* (C) 2025 */
package pl.dealsniper.core.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@RequiredArgsConstructor
public class ManagedTask {

    @Getter
    private final String key;

    private final Long sourceId;
    private final String taskName;
    private final ThreadPoolTaskScheduler scheduler;
    private final Consumer<Long> taskAction;
    private final Duration interval;

    private ScheduledFuture<?> future;

    public void start(Instant startTime) {
        if (isRunning()) {
            log.info("Task {} already running", key);
            return;
        }
        Runnable runnable = () -> taskAction.accept(sourceId);
        future = scheduler.scheduleAtFixedRate(runnable, startTime, interval);
        log.info("Started task: {}", taskName);
    }

    public void stop() {
        Optional.ofNullable(future).ifPresent(f -> f.cancel(true));
        future = null;
        log.info("Stopped task {}", key);
    }

    public boolean isRunning() {
        return future != null && !future.isCancelled();
    }
}
