/* (C) 2025 */
package pl.dealsniper.core.mapper;

import com.dealsniper.jooq.tables.records.SourcesRecord;
import org.mapstruct.Mapper;
import pl.dealsniper.core.dto.response.source.SourceResponse;
import pl.dealsniper.core.model.Source;

@Mapper(componentModel = "spring")
public interface SourceMapper {

    Source toDomainSource(SourcesRecord jooqSource);

    SourcesRecord toJooqSourceRecord(Source source);

    SourceResponse toSourceResponse(Source source);
}
