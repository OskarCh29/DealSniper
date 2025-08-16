package pl.dealsniper.core.mapper;

import org.mapstruct.Mapper;

import com.dealsniper.jooq.tables.records.SourcesRecord;

import pl.dealsniper.core.dto.request.SourceRequest;
import pl.dealsniper.core.model.Source;

@Mapper(componentModel = "spring")
public interface SourceMapper {

    Source toDomainSource(SourceRequest sourceRequest);

    Source toDomainSource(SourcesRecord jooqSource);

    SourcesRecord toJooqSourceRecord(Source source);
}
