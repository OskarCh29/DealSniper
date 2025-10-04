/* (C) 2025 */
package pl.dealsniper.core.mapper;

import com.dealsniper.jooq.tables.records.ScheduledTasksRecord;
import org.mapstruct.Mapper;
import pl.dealsniper.core.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toDomainModel(ScheduledTasksRecord record);

    ScheduledTasksRecord toJooqRecord(Task task);
}
