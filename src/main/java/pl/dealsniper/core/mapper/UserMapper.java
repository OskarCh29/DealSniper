/* (C) 2025 */
package pl.dealsniper.core.mapper;

import com.dealsniper.jooq.tables.records.UsersRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.dealsniper.core.dto.request.user.UserRequest;
import pl.dealsniper.core.dto.response.user.UserResponse;
import pl.dealsniper.core.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomainModel(UserRequest userRequest);

    User toDomainModel(UsersRecord jooqRecord);

    UsersRecord toJooqUserRecord(User user);

    @Mapping(source = "active", target = "userStatus")
    UserResponse toUserResponse(User user);
}
