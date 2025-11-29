/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.List;
import java.util.UUID;
import pl.dealsniper.core.model.Task;

public interface TaskRepository {

    List<Task> findAllActiveTasks();

    Integer countUserStartedTasksByUserId(UUID userId);

    boolean existsActiveTaskByUserAndSourceId(UUID userId, Long sourceId);

    Task save(Task task);

    void deactivateTask(UUID userId, Long sourceId);

    void activateTask(UUID userId, String taskName);

    boolean existsInactiveTaskByNameAndUserId(String taskName, UUID userId);

    void deleteTask(UUID userId, Long sourceId);

    boolean existsByNameAndUserId(String taskName, UUID userId);
}
