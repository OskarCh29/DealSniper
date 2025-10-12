/* (C) 2025 */
package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.Users.USERS;

import com.dealsniper.jooq.tables.records.UsersRecord;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.exception.InsertFailedException;
import pl.dealsniper.core.mapper.UserMapper;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.UserRepository;
import pl.dealsniper.core.util.CryptoUtil;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dsl;

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UsersRecord record = userMapper.toJooqUserRecord(user);
        record.setId(UUID.randomUUID());
        UsersRecord savedRecord = dsl.insertInto(USERS)
                .set(USERS.ID, record.getId())
                .set(USERS.EMAIL, record.getEmail())
                .set(USERS.PASSWORD, record.getPassword())
                .returning()
                .fetchOne();

        if (savedRecord == null) {
            throw new InsertFailedException("USER", user.getId());
        }

        return userMapper.toDomainModel(savedRecord);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return dsl.selectFrom(USERS).where(USERS.ID.eq(id)).fetchOptional().map(userMapper::toDomainModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOptional()
                .map(userMapper::toDomainModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return dsl.fetchExists(dsl.selectFrom(USERS).where(USERS.EMAIL.eq(email)));
    }

    @Override
    public void deleteUserPersonalData(UUID userId) {
        dsl.update(USERS)
                .set(
                        USERS.EMAIL,
                        DSL.concat(
                                DSL.inline("deleted_user_"), USERS.ID.cast(String.class), DSL.inline("@example.com")))
                .set(USERS.PASSWORD, CryptoUtil.getRandomHash())
                .set(USERS.ACTIVE, false)
                .set(USERS.DELETED_AT, LocalDateTime.now())
                .where(USERS.ID.eq(userId))
                .execute();
    }
}
