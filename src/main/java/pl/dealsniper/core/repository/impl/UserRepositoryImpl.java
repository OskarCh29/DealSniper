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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.exception.db.InsertFailedException;
import pl.dealsniper.core.exception.db.ResourceUsedException;
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
    public User save(User user, UUID userId) {
        UsersRecord record = userMapper.toJooqUserRecord(user);
        record.setId(userId);
        try {
            UsersRecord savedRecord = dsl.insertInto(USERS)
                    .set(USERS.ID, record.getId())
                    .set(USERS.EMAIL, record.getEmail())
                    .set(USERS.PASSWORD, record.getPassword())
                    .returning()
                    .fetchOne();

            return userMapper.toDomainModel(savedRecord);
        } catch (DuplicateKeyException e) {
            throw new ResourceUsedException("User with provided email already exists");
        } catch (DataAccessException e) {
            throw new InsertFailedException("USER", user.getId());
        }
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
    public void deleteUserPersonalData(UUID userId, LocalDateTime deletedAt) {
        dsl.update(USERS)
                .set(
                        USERS.EMAIL,
                        DSL.concat(
                                DSL.inline("deleted_user_"), USERS.ID.cast(String.class), DSL.inline("@example.com")))
                .set(USERS.PASSWORD, CryptoUtil.getRandomHash())
                .set(USERS.ACTIVE, false)
                .set(USERS.DELETED_AT, deletedAt)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    @Override
    public User update(User user) {
        UsersRecord record = dsl.update(USERS)
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.PASSWORD, user.getPassword())
                .set(USERS.ACTIVE, user.getActive())
                .set(USERS.DELETED_AT, user.getDeletedAt())
                .where(USERS.ID.eq(user.getId()))
                .returning()
                .fetchOne();
        if (record == null) {
            throw new InsertFailedException("USER", user.getId());
        }
        return userMapper.toDomainModel(record);
    }

    @Override
    public boolean existsActiveById(UUID userId) {
        return dsl.fetchExists(dsl.selectFrom(USERS).where(USERS.ID.eq(userId)).and(USERS.ACTIVE.isTrue()));
    }
}
