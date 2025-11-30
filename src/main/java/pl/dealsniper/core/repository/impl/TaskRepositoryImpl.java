/* (C) 2025 */
package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.ScheduledTasks.SCHEDULED_TASKS;

import com.dealsniper.jooq.tables.records.ScheduledTasksRecord;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.exception.db.InsertFailedException;
import pl.dealsniper.core.mapper.TaskMapper;
import pl.dealsniper.core.model.Task;
import pl.dealsniper.core.repository.TaskRepository;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final DSLContext dsl;
    private final TaskMapper mapper;

    @Override
    public Task save(Task task) {
        ScheduledTasksRecord record = mapper.toJooqRecord(task);
        try {
            ScheduledTasksRecord savedRecord =
                    dsl.insertInto(SCHEDULED_TASKS).set(record).returning().fetchOne();
            return mapper.toDomainModel(savedRecord);

        } catch (DataAccessException e) {
            throw new InsertFailedException("TASK", task.getUserId() + ":" + task.getSourceId());
        }
    }

    @Override
    public Integer activateTask(UUID userId, Long sourceId) {
        return updateActiveFlag(userId, sourceId, true);
    }

    @Override
    public Integer deactivateTask(UUID userId, Long sourceId) {
        return updateActiveFlag(userId, sourceId, false);
    }

    @Override
    public Integer deleteTask(UUID userId, Long sourceId) {
        return dsl.deleteFrom(SCHEDULED_TASKS)
                .where(SCHEDULED_TASKS.USER_ID.eq(userId))
                .and(SCHEDULED_TASKS.SOURCE_ID.eq(sourceId))
                .execute();
    }

    @Override
    public Integer countUserTasks(UUID userId) {
        return dsl.fetchCount(DSL.selectFrom(SCHEDULED_TASKS).where(SCHEDULED_TASKS.USER_ID.eq(userId)));
    }

    @Override
    public boolean existsByConstraints(UUID userId, Long sourceId, String taskName) {
        return dsl.fetchExists(dsl.selectOne()
                .from(SCHEDULED_TASKS)
                .where((SCHEDULED_TASKS.USER_ID.eq(userId).and(SCHEDULED_TASKS.SOURCE_ID.eq(sourceId)))
                        .or((SCHEDULED_TASKS.USER_ID.eq(userId).and(SCHEDULED_TASKS.TASK_NAME.eq(taskName))))));
    }

    @Override
    public List<Task> findAllActiveTasks() {
        return dsl.selectFrom(SCHEDULED_TASKS)
                .where(SCHEDULED_TASKS.ACTIVE.eq(true))
                .fetch()
                .map(mapper::toDomainModel);
    }

    private int updateActiveFlag(UUID userId, Long sourceId, boolean active) {
        return dsl.update(SCHEDULED_TASKS)
                .set(SCHEDULED_TASKS.ACTIVE, active)
                .where(SCHEDULED_TASKS.USER_ID.eq(userId).and(SCHEDULED_TASKS.SOURCE_ID.eq(sourceId)))
                .execute();
    }
}
