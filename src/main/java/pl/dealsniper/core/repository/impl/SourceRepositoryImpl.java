/* (C) 2025 */
package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.ScheduledTasks.SCHEDULED_TASKS;
import static com.dealsniper.jooq.tables.Sources.SOURCES;
import static com.dealsniper.jooq.tables.Users.USERS;

import com.dealsniper.jooq.tables.records.SourcesRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.exception.db.InsertFailedException;
import pl.dealsniper.core.exception.db.ResourceUsedException;
import pl.dealsniper.core.exception.scheduler.ScheduledTaskException;
import pl.dealsniper.core.mapper.SourceMapper;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.repository.SourceRepository;

@Repository
@RequiredArgsConstructor
public class SourceRepositoryImpl implements SourceRepository {

    private final DSLContext dsl;

    private final SourceMapper mapper;

    @Override
    public Source save(Source source) {
        SourcesRecord record = mapper.toJooqSourceRecord(source);
        try {
            SourcesRecord inserted = dsl.insertInto(SOURCES)
                    .set(SOURCES.FILTERED_URL, record.getFilteredUrl())
                    .set(SOURCES.USER_ID, record.getUserId())
                    .returning()
                    .fetchOne();

            return mapper.toDomainSource(inserted);
        } catch (DuplicateKeyException e) {
            throw new ResourceUsedException("Provided source url already exists on your account");
        } catch (DataAccessException e) {
            throw new InsertFailedException("SOURCE:" + source.getId(), "Source userId:" + source.getUserId());
        }
    }

    @Override
    public List<Source> findByUserId(UUID userId) {
        return dsl.selectFrom(SOURCES).where(SOURCES.USER_ID.eq(userId)).fetch().map(mapper::toDomainSource);
    }

    @Override
    public int deleteById(Long id) {
        boolean hasActiveTasks = dsl.fetchExists(dsl.selectFrom(SCHEDULED_TASKS)
                .where(SCHEDULED_TASKS.SOURCE_ID.eq(id))
                .and(SCHEDULED_TASKS.ACTIVE.eq(true)));
        if (hasActiveTasks) {
            throw new ScheduledTaskException("Cannot delete source with active tasks working");
        }

        return dsl.deleteFrom(SOURCES).where(SOURCES.ID.eq(id)).execute();
    }

    @Override
    public Optional<Source> findById(Long id) {
        return dsl.selectFrom(SOURCES).where(SOURCES.ID.eq(id)).fetchOptional().map(mapper::toDomainSource);
    }

    @Override
    public Optional<Source> findByIdAndUserId(Long id, UUID userId) {
        return dsl.selectFrom(SOURCES)
                .where(SOURCES.ID.eq(id))
                .and(SOURCES.USER_ID.eq(userId))
                .fetchOptional()
                .map(mapper::toDomainSource);
    }

    @Override
    public boolean existsForUserAndActive(UUID userId, Long sourceId) {
        return dsl.fetchExists(dsl.selectOne()
                .from(SOURCES)
                .join(USERS)
                .on(SOURCES.USER_ID.eq(USERS.ID))
                .where(SOURCES.ID.eq(sourceId))
                .and(USERS.ID.eq(userId))
                .and(USERS.ACTIVE.isTrue()));
    }
}
