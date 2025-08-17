package pl.dealsniper.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dealsniper.jooq.tables.records.UsersRecord;

import pl.dealsniper.core.dto.request.UserRequest;
import pl.dealsniper.core.dto.response.UserResponse;
import pl.dealsniper.core.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomainModel(UserRequest userRequest);

    User toDomainModel(UsersRecord jooqRecord);

    UsersRecord toJooqUserRecord(User user);

    @Mapping(source = "active", target = "userStatus")
    UserResponse toUserResponse(User user);
}
