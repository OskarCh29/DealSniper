/* (C) 2025 */
package pl.dealsniper.core.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.exception.ResourceUsedException;
import pl.dealsniper.core.exception.ScheduledTaskException;
import pl.dealsniper.core.model.Task;
import pl.dealsniper.core.repository.TaskRepository;
import pl.dealsniper.core.scheduler.ManagedTask;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    @Value("${app.config.scheduler-hour-interval}")
    private int schedulerInterval;

    @Value("${app.config.tasks-per-user}")
    private int userTasks;

    private static final int DELAY_MULTIPLAYER = 120;
    private static final int STAGGER_STEP = 30;

    private final TaskRepository taskRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final CarDealOrchestrator orchestrator;

    private final Map<String, ManagedTask> activeTasks = new ConcurrentHashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleActiveTasks() {
        List<Task> dbActiveTasks = taskRepository.findAllActiveTasks();

        for (int i = 0; i < dbActiveTasks.size(); i++) {
            Task task = dbActiveTasks.get(i);
            String key = task.getUserId() + ":" + task.getSourceId();

            if (activeTasks.containsKey(key) && activeTasks.get(key).isRunning()) {
                log.info("Task {} already running, skipping restore", key);
                continue;
            }

            ManagedTask managedTask = createManagedTask(task.getUserId(), task.getSourceId(), task.getTaskName());
            long delaySeconds = (long) (Math.random() * DELAY_MULTIPLAYER) + (long) i * STAGGER_STEP;
            Instant startTime = Instant.now().plusSeconds(delaySeconds);

            managedTask.start(startTime);
            activeTasks.put(key, managedTask);

            log.info("Found active task: {}.Restoring...", key);
        }
    }

    public void startScheduledTask(UUID userId, Long sourceId, String taskName) {
        checkIfTaskAlreadyExists(userId, sourceId);
        checkIfUserCanStartNewTask(userId);

        String key = getTaskKey(userId, sourceId);

        if (checkIfTaskAlreadyRunning(key)) {
            log.info("Task already running for {}", taskName);
            return;
        }
        ManagedTask task = createManagedTask(userId, sourceId, taskName);
        task.start(calculateInitialDelay());
        activeTasks.put(key, task);

        saveNewTask(userId, sourceId, taskName);
    }

    public void stopScheduledTask(UUID userId, Long sourceId) {
        String key = getTaskKey(userId, sourceId);
        ManagedTask task = activeTasks.get(key);
        if (task != null) {
            task.stop();
            stopTask(userId, sourceId);
        } else {
            log.info("Task {} was not running", key);
            throw new ScheduledTaskException("Requested task was not running");
        }
    }

    public void resumeInactiveTask(UUID userId, Long sourceId, String taskName) {
        String key = getTaskKey(userId, sourceId);
        validateTaskResumable(userId, taskName, key);
        checkIfUserCanStartNewTask(userId);

        ManagedTask task = activeTasks.get(key);
        if (task == null) {
            task = createManagedTask(userId, sourceId, taskName);
            activeTasks.put(key, task);
        }
        task.resume();
        taskRepository.activateTask(userId, taskName);
    }

    private ManagedTask createManagedTask(UUID userId, Long sourceId, String taskName) {
        String key = userId + ":" + sourceId;
        return new ManagedTask(
                key,
                sourceId,
                taskName,
                taskScheduler,
                (src) -> orchestrator.processSingleSource(src, taskName),
                Duration.ofHours(schedulerInterval));
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
    private void saveNewTask(UUID userId, Long sourceId, String taskName) {
        Task newTask = Task.builder()
                .taskName(taskName)
                .userId(userId)
                .sourceId(sourceId)
                .build();
        taskRepository.save(newTask);
    }

    @Transactional
    private void stopTask(UUID userId, Long sourceId) {
        taskRepository.deactivateTask(userId, sourceId);
    }

    private Instant calculateInitialDelay() {
        long randomDelay = (long) (Math.random() * DELAY_MULTIPLAYER);
        return Instant.now().plusSeconds(randomDelay);
    }

    private String getTaskKey(UUID userId, Long sourceId) {
        return userId + ":" + sourceId;
    }

    private void validateTaskResumable(UUID userId, String taskName, String key) {
        validate(
                !checkIfTaskAlreadyRunning(key),
                () -> new ScheduledTaskException("Task '" + taskName + "' is already running and cannot be resumed"));

        validate(
                taskRepository.existsInactiveTaskByNameAndUserId(taskName, userId),
                () -> new ScheduledTaskException("Task '" + taskName + "' does not exist and cannot be resumed"));
    }

    private boolean checkIfTaskAlreadyRunning(String key) {
        return activeTasks.containsKey(key) && activeTasks.get(key).isRunning();
    }

    private void validate(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!condition) {
            throw exceptionSupplier.get();
        }
    }
}
