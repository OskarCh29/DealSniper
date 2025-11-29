/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import pl.dealsniper.core.model.User;

public interface UserRepository {

    User save(User users, UUID userId);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsActiveById(UUID userId);

    void deleteUserPersonalData(UUID userId, LocalDateTime deletedAt);

    User update(User user);
}
