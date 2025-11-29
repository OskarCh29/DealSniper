/* (C) 2025 */
package pl.dealsniper.core.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class ManagedTask {

    private static final int RESUME_DELAY = 5;

    @Getter
    private final String key;
    private final Long sourceId;
    private final String taskName;
    private final ThreadPoolTaskScheduler scheduler;
    private final Consumer<Long> taskAction;
    private final Duration interval;

    private ScheduledFuture<?> future;

    public void start(Instant startTime) {
        if (future != null && !future.isCancelled()) {
            log.info("Task {} already running", key);
            return;
        }
        Runnable runnable = () -> {
            try {
                taskAction.accept(sourceId);
            } catch (Exception e) {
                log.error("Error while executing task {}:{}", key, e.getMessage(), e);
            }
        };
        future = scheduler.scheduleAtFixedRate(runnable, startTime, interval);
        log.info("Started task: {}", taskName);
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
            future = null;
            log.info("Stopped task {}", key);
        }
    }

    public void resume() {
        if (!isRunning()) {
            start(Instant.now().plusSeconds(RESUME_DELAY));
        }
    }

    public boolean isRunning() {
        return future != null && !future.isCancelled();
    }
}
