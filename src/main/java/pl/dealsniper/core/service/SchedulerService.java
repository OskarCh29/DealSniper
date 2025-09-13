/* (C) 2025 */
package pl.dealsniper.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.exception.ResourceUsedException;
import pl.dealsniper.core.model.Task;
import pl.dealsniper.core.repository.TaskRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {


    @Value("${app.config.scheduler-hour-interval}")
    private int schedulerInterval;

    private final TaskRepository taskRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final CarDealOrchestrator orchestrator;
    private final Map<String, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();

    @Value("${app.config.tasks-per-user}")
    private int userTasks;

    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleActiveTasks() {
        List<Task> activeTasks = taskRepository.findAllActiveTasks();
        activeTasks.forEach(task -> restoreActiveTasks(task.getUserId(), task.getSourceId()));
    }

    public void startScheduledTask(UUID userId, Long sourceId) {
        checkIfTaskAlreadyExists(userId, sourceId);
        String key = userId + ":" + sourceId;

        if (activeTasks.containsKey(key)) {
            log.info("Task already scheduled for {}", key);
            return;
        }
        checkIfUserCanStartNewTask(userId);
        Runnable task = () -> {
            try {
                orchestrator.processSingleSource(sourceId);
            } catch (Exception e) {
                log.error("Error scheduled task for {}:{}", userId, sourceId, e);
            }
        };
        ScheduledFuture<?> future =
                taskScheduler.scheduleAtFixedRate(task, calculateInitialDelay(), getScheduleInterval());
        activeTasks.put(key, future);
        log.info("Scheduled task for {}", key);
        saveNewTask(userId, sourceId);
    }

    public void stopScheduledTask(UUID userId, Long sourceId) {
        String key = userId + ":" + sourceId;
        ScheduledFuture<?> future = activeTasks.remove(key);
        if (future != null) {
            future.cancel(false);
            log.info("Stopped task for {}", key);
            stopTask(userId, sourceId);
        } else {
            log.info("Task for {} was already stopped", key);
        }
    }

    @Transactional(readOnly = true)
    private void checkIfTaskAlreadyExists(UUID uuid, Long id) {
        if (taskRepository.existsActiveTaskByUserAndSourceId(uuid, id)) {
            throw new ResourceUsedException("This task is already scheduled");
        }
    }

    @Transactional(readOnly = true)
    private void checkIfUserCanStartNewTask(UUID userId) {
        if (taskRepository.countUserStartedTasksByUserId(userId) == userTasks) {
            throw new ResourceUsedException("You reached maximum number of started tasks");
        }
    }

    @Transactional
    private void saveNewTask(UUID userId, Long sourceId) {
        Task newTask = Task.builder().userId(userId).sourceId(sourceId).build();
        taskRepository.save(newTask);
    }

    @Transactional
    private void stopTask(UUID userId, Long sourceId) {
        taskRepository.deactivateTask(userId, sourceId);
    }

    private void restoreActiveTasks(UUID userId, Long sourceId){
        String key = userId + ":" + sourceId;
        if (activeTasks.containsKey(key)) {
            log.info("Task already restored for {}", key);
            return;
        }
        Runnable task = () -> {
            try {
                orchestrator.processSingleSource(sourceId);
            } catch (Exception e) {
                log.error("Error scheduled task for {}:{}", userId, sourceId, e);
            }
        };
        ScheduledFuture<?> future =
                taskScheduler.scheduleAtFixedRate(task, calculateInitialDelay(), getScheduleInterval());
        activeTasks.put(key, future);
        log.info("Restored task for {}", key);

    }

    private Instant calculateInitialDelay() {
        long randomDelay = (long) (Math.random()*120);
        return Instant.now().plusSeconds(randomDelay);
    }

    private Duration getScheduleInterval() {
        return Duration.ofHours(schedulerInterval);
    }

}
