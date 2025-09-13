/* (C) 2025 */
package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.Sources.SOURCES;

import com.dealsniper.jooq.tables.records.SourcesRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.exception.InsertFailedException;
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

        SourcesRecord inserted = dsl.insertInto(SOURCES)
                .set(SOURCES.FILTERED_URL, record.getFilteredUrl())
                .set(SOURCES.USER_ID, record.getUserId())
                .returning()
                .fetchOne();

        if (inserted == null) {
            throw new InsertFailedException("SOURCE:"+ source.getId(), "Source userId:" + source.getUserId());
        }

        return mapper.toDomainSource(inserted);
    }

    @Override
    public Optional<Source> findByUserIdAndFilterUrl(UUID id, String filterUrl) {
        return dsl.selectFrom(SOURCES)
                .where(SOURCES.USER_ID.eq(id))
                .and(SOURCES.FILTERED_URL.eq(filterUrl))
                .fetchOptional()
                .map(mapper::toDomainSource);
    }

    @Override
    public List<Source> findByUserId(UUID userId) {
        return dsl.selectFrom(SOURCES).where(SOURCES.USER_ID.eq(userId)).fetch().map(mapper::toDomainSource);
    }

    @Override
    public void deleteByUserId(UUID id) {
        dsl.deleteFrom(SOURCES).where(SOURCES.USER_ID.eq(id)).execute();
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
}
