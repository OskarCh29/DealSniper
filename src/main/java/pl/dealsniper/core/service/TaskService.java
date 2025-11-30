/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.exception.db.RecordNotFoundException;
import pl.dealsniper.core.exception.db.ResourceUsedException;
import pl.dealsniper.core.model.Task;
import pl.dealsniper.core.repository.TaskRepository;
import pl.dealsniper.core.util.ValidationUtil;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private static final int NO_ROWS_UPDATED = 0;

    @Transactional
    public Task saveNewTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public void activateTask(UUID userId, Long sourceId) {
        int affectedRows = taskRepository.activateTask(userId, sourceId);
        ValidationUtil.throwIfTrue(
                affectedRows == NO_ROWS_UPDATED,
                () -> new RecordNotFoundException("Task you want to start does not exist"));
    }

    @Transactional
    public void deactivateTask(UUID userId, Long sourceId) {
        int affectedRows = taskRepository.deactivateTask(userId, sourceId);
        ValidationUtil.throwIfTrue(
                affectedRows == NO_ROWS_UPDATED,
                () -> new RecordNotFoundException("Task you want to stop does not exist"));
    }

    @Transactional
    public void deleteTask(UUID userId, Long sourceId) {
        int affectedRows = taskRepository.deleteTask(userId, sourceId);
        ValidationUtil.throwIfTrue(
                affectedRows == NO_ROWS_UPDATED,
                () -> new RecordNotFoundException("Task you want to delete does not exist"));
    }

    @Transactional(readOnly = true)
    public Integer countUserTasks(UUID userId) {
        return taskRepository.countUserTasks(userId);
    }

    @Transactional(readOnly = true)
    public void validateUniqueTask(UUID userId, Long sourceId, String taskName) {
        ValidationUtil.throwIfTrue(
                taskRepository.existsByConstraints(userId, sourceId, taskName),
                () -> new ResourceUsedException("Task with same source or name already exists"));
    }

    @Transactional(readOnly = true)
    public List<Task> fetchActiveTasks() {
        return taskRepository.findAllActiveTasks();
    }
}
