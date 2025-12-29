/* (C) 2025 */
package pl.dealsniper.core.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import pl.dealsniper.core.configuration.ApplicationConfiguration;
import pl.dealsniper.core.dto.request.task.TaskChangeStateRequest;
import pl.dealsniper.core.dto.request.task.TaskCreateRequest;
import pl.dealsniper.core.dto.response.task.TaskResponse;
import pl.dealsniper.core.dto.response.task.TaskStatus;
import pl.dealsniper.core.exception.scheduler.ScheduledTaskException;
import pl.dealsniper.core.scheduler.ManagedTask;
import pl.dealsniper.core.time.TimeProvider;
import pl.dealsniper.core.util.ValidationUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private static final int DELAY_MULTIPLAYER = 120;

    private final TaskService taskService;
    private final ApplicationConfiguration properties;
    private final CarDealOrchestrator carDealOrchestrator;
    private final ThreadPoolTaskScheduler scheduler;
    private final TimeProvider timeProvider;
    private final Map<String, ManagedTask> activeTasks = new ConcurrentHashMap<>();

    public TaskResponse startNewScheduledTask(TaskCreateRequest taskRequest) {
        taskService.validateUniqueTask(taskRequest.userId(), taskRequest.sourceId(), taskRequest.taskName());
        Integer userTasks = taskService.countUserTasks(taskRequest.userId());
        ValidationUtil.throwIfTrue(
                userTasks == properties.getConfig().getTasksPerUser(),
                () -> new ScheduledTaskException("Reached maximum tasks amount for provided user"));

        taskService.saveNewTask(taskRequest.userId(), taskRequest.sourceId(), taskRequest.taskName());

        log.info("Starting task: {} for user: {}", taskRequest.taskName(), taskRequest.userId());
        ManagedTask task = createManagedTask(taskRequest.userId(), taskRequest.sourceId(), taskRequest.taskName());
        task.start(getInitialDelay());
        activeTasks.put(generateKey(taskRequest.userId(), taskRequest.sourceId()), task);

        return new TaskResponse(
                taskRequest.taskName(), taskRequest.userId().toString(), TaskStatus.STARTED.getMessage());
    }

    public TaskResponse changeTaskState(TaskChangeStateRequest stateRequest) {
        String key = generateKey(stateRequest.userId(), stateRequest.sourceId());
        ManagedTask task = activeTasks.get(key);

        return switch (stateRequest.state()) {
            case RUN -> resume(stateRequest.userId(), stateRequest.sourceId(), stateRequest.taskName(), key, task);
            case STOP -> stop(stateRequest.userId(), stateRequest.sourceId(), stateRequest.taskName(), key, task);
        };
    }

    public void deleteTask(UUID userId, Long sourceId) {
        String key = generateKey(userId, sourceId);
        ManagedTask task = activeTasks.get(key);
        if (task != null && task.isRunning()) {
            log.info("Stopping running task before deleting for user: {}", userId);
            task.stop();
        }
        activeTasks.remove(key);

        taskService.deleteTask(userId, sourceId);
    }

    private TaskResponse resume(UUID userId, Long sourceId, String taskName, String key, ManagedTask task) {
        taskService.activateTask(userId, sourceId);
        ValidationUtil.throwIfTrue(isRunning(key), () -> new ScheduledTaskException("Task was already running"));

        log.info("Resuming task: {} for user: {}", taskName, userId);
        if (task == null) {
            task = createManagedTask(userId, sourceId, taskName);
            activeTasks.put(key, task);
        }
        task.start(getInitialDelay());

        return new TaskResponse(taskName, userId.toString(), TaskStatus.RESUMED.getMessage());
    }

    private TaskResponse stop(UUID userId, Long sourceId, String taskName, String key, ManagedTask task) {
        taskService.deactivateTask(userId, sourceId);
        ValidationUtil.throwIfTrue(
                task == null, () -> new ScheduledTaskException("Task to be stopped was not running"));
        log.info("Stopping task: {} for user: {}", taskName, userId);
        task.stop();
        activeTasks.remove(key);

        return new TaskResponse(taskName, userId.toString(), TaskStatus.STOPPED.getMessage());
    }

    private boolean isRunning(String key) {
        return activeTasks.containsKey(key) && activeTasks.get(key).isRunning();
    }

    private ManagedTask createManagedTask(UUID userId, Long sourceId, String taskName) {
        return new ManagedTask(
                generateKey(userId, sourceId),
                sourceId,
                taskName,
                scheduler,
                src -> carDealOrchestrator.processSingleSource(src, taskName),
                Duration.ofHours(properties.getConfig().getSchedulerHourInterval()));
    }

    private String generateKey(UUID userId, Long sourceId) {
        return userId + ":" + sourceId;
    }

    private Instant getInitialDelay() {
        return timeProvider.now().plusSeconds(randomDelay());
    }

    private long randomDelay() {
        return (long) (Math.random() * DELAY_MULTIPLAYER);
    }
}
