/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.dto.request.user.UserRequest;
import pl.dealsniper.core.exception.db.RecordNotFoundException;
import pl.dealsniper.core.exception.db.ResourceUsedException;
import pl.dealsniper.core.exception.user.UserInactiveException;
import pl.dealsniper.core.mapper.UserMapper;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.UserRepository;
import pl.dealsniper.core.time.TimeProvider;
import pl.dealsniper.core.util.ValidationUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public User saveUser(UserRequest userRequest) {
        User newUser = userMapper.toDomainModel(userRequest);
        UUID userId = UUID.randomUUID();
        return userRepository.save(newUser, userId);
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User with provided Id not found"));
    }

    @Transactional
    public void deleteUserAccount(UUID uuid) {
        User user = getUserById(uuid);
        userRepository.deleteUserPersonalData(user.getId(), timeProvider.timeNow());
    }

    @Transactional(readOnly = true)
    public void ensureEmailAvailable(String email) {
        ValidationUtil.throwIfTrue(
                userRepository.existsByEmail(email), () -> new ResourceUsedException("Email already in use"));
    }

    @Transactional(readOnly = true)
    public void ensureUserActive(UUID userId) {
        ValidationUtil.throwIfFalse(
                userRepository.existsActiveById(userId),
                () -> new UserInactiveException("Provided user is not active"));
    }
}
