package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.Users.USERS;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import com.dealsniper.jooq.tables.records.UsersRecord;

import lombok.RequiredArgsConstructor;
import pl.dealsniper.core.exception.InsertFailedException;
import pl.dealsniper.core.mapper.UserMapper;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.UserRepository;
import pl.dealsniper.core.service.CryptoService;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final CryptoService cryptoService;

    private final DSLContext dsl;

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UsersRecord record = userMapper.toJooqUserRecord(user);

        UsersRecord savedRecord = dsl.insertInto(USERS).set(record).returning().fetchOne();

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
    public void deactivateUserAccount(UUID id) {
        dsl.update(USERS)
                .set(USERS.ACTIVE, false)
                .set(USERS.DELETED_AT, LocalDateTime.now())
                .where(USERS.ID.eq(id))
                .execute();
    }

    @Override
    public void deleteUserPersonalData() {
        dsl.update(USERS)
                .set(
                        USERS.EMAIL,
                        DSL.concat(
                                DSL.inline("deleted_user_"), USERS.ID.cast(String.class), DSL.inline("@example.com")))
                .set(USERS.PASSWORD, cryptoService.getRandomHash())
                .where(USERS.ACTIVE.eq(false))
                .execute();
    }
}
