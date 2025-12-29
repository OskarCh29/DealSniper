/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.dealsniper.core.model.Task;

public interface TaskRepository {

    Task save(Task task);

    Integer activateTask(UUID userId, Long sourceId);

    Integer deactivateTask(UUID userId, Long sourceId);

    Integer deleteTask(UUID userId, Long sourceId);

    Integer countUserTasks(UUID userId);

    boolean existsByConstraints(UUID userId, Long sourceId, String taskName);

    Page<Task> findAllActiveTasks(Pageable pageable);
}
