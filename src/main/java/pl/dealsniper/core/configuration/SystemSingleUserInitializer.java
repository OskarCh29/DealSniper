/* (C) 2025 */
package pl.dealsniper.core.configuration;

import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.UserRepository;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.config.single-user.enabled", havingValue = "true", matchIfMissing = true)
public class SystemSingleUserInitializer {

    private final UserRepository userRepository;

    @Value("${app.config.single-user.email}")
    private String singleUserEmail;

    @PostConstruct
    public void init() {
        var user = userRepository
                .findById(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .orElseThrow();

        var updatedUser = User.builder()
                .id(user.getId())
                .email(singleUserEmail)
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .active(user.getActive())
                .deletedAt(user.getDeletedAt())
                .build();

        userRepository.update(updatedUser);
    }
}
