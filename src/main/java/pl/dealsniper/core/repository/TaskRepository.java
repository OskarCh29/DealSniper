/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.List;
import java.util.UUID;
import pl.dealsniper.core.model.Task;

public interface TaskRepository {

    Task save(Task task);

    Integer activateTask(UUID userId, Long sourceId);

    Integer deactivateTask(UUID userId, Long sourceId);

    Integer deleteTask(UUID userId, Long sourceId);

    Integer countUserTasks(UUID userId);

    boolean existsByConstraints(UUID userId, Long sourceId, String taskName);

    List<Task> findAllActiveTasks();
}
