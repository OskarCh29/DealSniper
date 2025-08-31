/* (C) 2025 */
package pl.dealsniper.core.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dealsniper.core.dto.request.UserRequest;
import pl.dealsniper.core.exception.RecordNotFoundException;
import pl.dealsniper.core.exception.ResourceUsedException;
import pl.dealsniper.core.mapper.UserMapper;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final UUID TEST_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final LocalDateTime TEST_CREATE_DATE = LocalDateTime.of(2025, 3, 15, 10, 0);
    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_HASHED_PASSWORD =
            "b56094a66443430e2b0871f31439fda21d7c4a50cd5ddadb511c85114e3906c9";

    @Mock
    UserRepository userRepository;

    @Spy
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    @Test
    void getUserByIdShouldReturnUserWhenIdValid() {
        User testUser = getTestUserBuilder()
                .id(TEST_UUID)
                .email(TEST_EMAIL)
                .password(TEST_HASHED_PASSWORD)
                .active(true)
                .createdAt(TEST_CREATE_DATE)
                .build();

        when(userRepository.findById(TEST_UUID)).thenReturn(Optional.ofNullable(testUser));

        User result = userService.getUserById(TEST_UUID);

        assertThat(result)
                .extracting(User::getId, User::getEmail, User::getPassword, User::getActive, User::getCreatedAt)
                .containsExactly(TEST_UUID, TEST_EMAIL, TEST_HASHED_PASSWORD, true, TEST_CREATE_DATE);

        verify(userRepository).findById(TEST_UUID);
    }

    @Test
    void getUserByIdShouldThrowExceptionWhenIdInvalid() {
        when(userRepository.findById(TEST_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(TEST_UUID))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("User with provided Id not found");

        verify(userRepository).findById(TEST_UUID);
    }

    @Test
    void saveUserShouldSaveUserWhenEmailAvailable() {
        UserRequest testRequest = getUserRequestWithSampleDate();

        doAnswer(invocation -> {
                    UserRequest req = invocation.getArgument(0);
                    return User.builder()
                            .email(req.email())
                            .password(req.password())
                            .build();
                })
                .when(userMapper)
                .toDomainModel(any(UserRequest.class));

        when(userRepository.existsByEmail(testRequest.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.saveUser(testRequest);

        assertThat(saved)
                .isNotNull()
                .extracting(User::getEmail, User::getPassword)
                .containsExactly(TEST_EMAIL, TEST_HASHED_PASSWORD);

        verify(userRepository).existsByEmail(testRequest.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveUserShouldThrowExceptionWhenEmailUnavailable() {
        UserRequest testRequest = getUserRequestWithSampleDate();

        when(userRepository.existsByEmail(testRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.saveUser(testRequest))
                .isInstanceOf(ResourceUsedException.class)
                .hasMessage("Email already in use");

        verify(userRepository).existsByEmail(testRequest.email());
    }

    @Test
    void deleteUserAccountShouldNotThrowException() {
        User user = getUserWithSampleData();
        when(userRepository.findById(TEST_UUID)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUserAccount(TEST_UUID));

        verify(userRepository).findById(TEST_UUID);
    }

    @Test
    void deleteUserAccountShouldThrowExceptionWhenUserNotExists() {
        when(userRepository.findById(TEST_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserAccount(TEST_UUID))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("User with provided Id not found");
    }

    private User getUserWithSampleData() {
        return User.builder()
                .id(TEST_UUID)
                .email(TEST_EMAIL)
                .password(TEST_HASHED_PASSWORD)
                .active(true)
                .createdAt(TEST_CREATE_DATE)
                .build();
    }

    private UserRequest getUserRequestWithSampleDate() {
        return UserRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_HASHED_PASSWORD)
                .build();
    }

    private User.UserBuilder getTestUserBuilder() {
        return User.builder();
    }
}
