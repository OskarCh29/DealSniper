/* (C) 2025 */
package pl.dealsniper.core.mapper;

import com.dealsniper.jooq.tables.records.SourcesRecord;
import org.mapstruct.Mapper;
import pl.dealsniper.core.dto.request.source.SourceRequest;
import pl.dealsniper.core.dto.response.SourceResponse;
import pl.dealsniper.core.model.Source;

@Mapper(componentModel = "spring")
public interface SourceMapper {

    Source toDomainSource(SourceRequest sourceRequest);

    Source toDomainSource(SourcesRecord jooqSource);

    SourcesRecord toJooqSourceRecord(Source source);

    SourceResponse toSourceResponse(Source source);
}
