package pl.dealsniper.core.repository;

import java.util.Optional;
import java.util.UUID;

import pl.dealsniper.core.model.User;

public interface UserRepository {

    User save(User users);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    void deactivateUserAccount(UUID id);

    void deleteUserPersonalData();
}
