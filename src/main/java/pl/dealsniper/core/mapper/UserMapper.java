package pl.dealsniper.core.mapper;

import org.mapstruct.Mapper;

import com.dealsniper.jooq.tables.records.UsersRecord;

import pl.dealsniper.core.dto.request.UserRequest;
import pl.dealsniper.core.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomainModel(UserRequest userRequest);

    User toDomainModel(UsersRecord jooqRecord);

    UsersRecord toJooqUserRecord(User user);
}
