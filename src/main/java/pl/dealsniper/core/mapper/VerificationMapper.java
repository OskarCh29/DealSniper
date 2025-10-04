/* (C) 2025 */
package pl.dealsniper.core.mapper;

import com.dealsniper.jooq.tables.records.VerificationsRecord;
import org.mapstruct.Mapper;
import pl.dealsniper.core.model.Verification;

@Mapper(componentModel = "spring")
public interface VerificationMapper {

    Verification toDomainModel(VerificationsRecord record);
}
