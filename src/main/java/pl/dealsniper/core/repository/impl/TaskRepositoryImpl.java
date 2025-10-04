/* (C) 2025 */
package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.ScheduledTasks.SCHEDULED_TASKS;

import com.dealsniper.jooq.tables.records.ScheduledTasksRecord;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.exception.InsertFailedException;
import pl.dealsniper.core.mapper.TaskMapper;
import pl.dealsniper.core.model.Task;
import pl.dealsniper.core.repository.TaskRepository;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final DSLContext dsl;
    private final TaskMapper mapper;

    @Override
    public List<Task> findAllActiveTasks() {
        return dsl.selectFrom(SCHEDULED_TASKS)
                .where(SCHEDULED_TASKS.ACTIVE.eq(true))
                .fetch()
                .map(mapper::toDomainModel);
    }

    @Override
    public boolean existsActiveTaskByUserAndSourceId(UUID userId, Long sourceId) {
        return dsl.fetchExists(dsl.selectFrom(SCHEDULED_TASKS)
                .where(SCHEDULED_TASKS.ACTIVE.eq(true))
                .and(SCHEDULED_TASKS.USER_ID.eq(userId))
                .and(SCHEDULED_TASKS.SOURCE_ID.eq(sourceId)));
    }

    @Override
    public boolean existsInactiveTaskByNameAndUserId(String taskName, UUID userId) {
        return dsl.fetchExists(dsl.selectFrom(SCHEDULED_TASKS)
                .where(SCHEDULED_TASKS.ACTIVE.eq(false))
                .and(SCHEDULED_TASKS.USER_ID.eq(userId))
                .and(SCHEDULED_TASKS.TASK_NAME.eq(taskName)));
    }

    @Override
    public Task save(Task task) {
        ScheduledTasksRecord record = mapper.toJooqRecord(task);
        ScheduledTasksRecord savedRecord = dsl.insertInto(SCHEDULED_TASKS)
                .set(SCHEDULED_TASKS.TASK_NAME, record.getTaskName())
                .set(SCHEDULED_TASKS.USER_ID, record.getUserId())
                .set(SCHEDULED_TASKS.SOURCE_ID, record.getSourceId())
                .set(SCHEDULED_TASKS.ACTIVE, true)
                .onDuplicateKeyIgnore()
                .returning()
                .fetchOne();

        if (savedRecord == null) {
            throw new InsertFailedException("TASK: ", task.getUserId() + ":" + task.getSourceId() + " already exists");
        }

        return mapper.toDomainModel(savedRecord);
    }

    @Override
    public void deactivateTask(UUID userId, Long sourceId) {
        dsl.update(SCHEDULED_TASKS)
                .set(SCHEDULED_TASKS.ACTIVE, false)
                .where(SCHEDULED_TASKS.USER_ID.eq(userId))
                .and(SCHEDULED_TASKS.SOURCE_ID.eq(sourceId))
                .execute();
    }

    @Override
    public Integer countUserStartedTasksByUserId(UUID userId) {
        return dsl.fetchCount(DSL.selectFrom(SCHEDULED_TASKS).where(SCHEDULED_TASKS.USER_ID.eq(userId)));
    }

    @Override
    public void activateTask(UUID userId, String taskName) {
        dsl.update(SCHEDULED_TASKS)
                .set(SCHEDULED_TASKS.ACTIVE, true)
                .where(SCHEDULED_TASKS.USER_ID.eq(userId))
                .and(SCHEDULED_TASKS.TASK_NAME.eq(taskName))
                .execute();
    }
}
