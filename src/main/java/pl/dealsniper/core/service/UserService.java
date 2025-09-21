/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.dto.request.UserRequest;
import pl.dealsniper.core.exception.RecordNotFoundException;
import pl.dealsniper.core.exception.ResourceUsedException;
import pl.dealsniper.core.exception.UserInactiveException;
import pl.dealsniper.core.mapper.UserMapper;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User saveUser(UserRequest userRequest) {
        ensureEmailAvailable(userRequest.email());
        User newUser = userMapper.toDomainModel(userRequest);

        return userRepository.save(newUser);
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
        userRepository.deleteUserPersonalData(user.getId());
    }

    public void ensureEmailAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResourceUsedException("Email already in use");
        }
    }

    public void ensureUserActive(UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User with provided Id not found"));
        if (!user.getActive()) {
            throw new UserInactiveException("Provided user is not active");
        }
    }
}
