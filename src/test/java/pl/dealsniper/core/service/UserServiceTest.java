/* (C) 2025 */
package pl.dealsniper.core.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.dealsniper.core.mock.MockConst.MOCK_CREATED_AT;
import static pl.dealsniper.core.mock.MockConst.MOCK_EMAIL;
import static pl.dealsniper.core.mock.MockConst.MOCK_HASH_PASSWORD;
import static pl.dealsniper.core.mock.MockConst.MOCK_UUID;
import static pl.dealsniper.core.mock.MockFactory.getMockUser;
import static pl.dealsniper.core.mock.MockFactory.getMockUserRequest;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dealsniper.core.dto.request.user.UserRequest;
import pl.dealsniper.core.exception.RecordNotFoundException;
import pl.dealsniper.core.exception.ResourceUsedException;
import pl.dealsniper.core.exception.UserInactiveException;
import pl.dealsniper.core.mapper.UserMapper;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Spy
    UserMapper userMapper;

    @InjectMocks
    UserService underTest;

    @Test
    void getUserByIdShouldReturnUserWhenIdValid() {
        User testUser = getTestUserBuilder()
                .id(MOCK_UUID)
                .email(MOCK_EMAIL)
                .password(MOCK_HASH_PASSWORD)
                .active(true)
                .createdAt(MOCK_CREATED_AT)
                .build();

        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.ofNullable(testUser));

        User result = underTest.getUserById(MOCK_UUID);

        assertThat(result)
                .extracting(User::getId, User::getEmail, User::getPassword, User::getActive, User::getCreatedAt)
                .containsExactly(MOCK_UUID, MOCK_EMAIL, MOCK_HASH_PASSWORD, true, MOCK_CREATED_AT);

        verify(userRepository).findById(MOCK_UUID);
    }

    @Test
    void getUserById_shouldThrowException_whenIdInvalid() {
        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getUserById(MOCK_UUID))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("User with provided Id not found");

        verify(userRepository).findById(MOCK_UUID);
    }

    @Test
    void saveUser_shouldSaveUser_whenEmailAvailable() {
        UserRequest testRequest = getMockUserRequest();

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
        when(userRepository.save(any(User.class), any(UUID.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = underTest.saveUser(testRequest);

        assertThat(saved)
                .isNotNull()
                .extracting(User::getEmail, User::getPassword)
                .containsExactly(MOCK_EMAIL, MOCK_HASH_PASSWORD);

        verify(userRepository).existsByEmail(testRequest.email());
        verify(userRepository).save(any(User.class), any(UUID.class));
    }

    @Test
    void saveUser_shouldThrowException_whenEmailUnavailable() {
        UserRequest testRequest = getMockUserRequest();

        when(userRepository.existsByEmail(testRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> underTest.saveUser(testRequest))
                .isInstanceOf(ResourceUsedException.class)
                .hasMessage("Email already in use");

        verify(userRepository).existsByEmail(testRequest.email());
    }

    @Test
    void deleteUserAccount_shouldNotThrowException() {
        User user = getMockUser();
        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> underTest.deleteUserAccount(MOCK_UUID));

        verify(userRepository).findById(MOCK_UUID);
    }

    @Test
    void deleteUserAccount_shouldThrowExceptio_whenUserNotExists() {
        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteUserAccount(MOCK_UUID))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("User with provided Id not found");
    }

    @Test
    void ensureUserActive_shouldThrowException_whenUserNotExists() {
        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.ensureUserActive(MOCK_UUID))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("User with provided Id not found");

        verify(userRepository).findById(MOCK_UUID);
    }

    @Test
    void ensureUserActive_userFound() {
        User user = getMockUser();

        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> underTest.ensureUserActive(MOCK_UUID));

        verify(userRepository).findById(MOCK_UUID);
    }

    @Test
    void ensureUserActive_userInactive() {
        User user = getTestUserBuilder()
                .id(MOCK_UUID)
                .email(MOCK_EMAIL)
                .password(MOCK_HASH_PASSWORD)
                .active(false)
                .createdAt(MOCK_CREATED_AT)
                .build();

        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.ensureUserActive(MOCK_UUID))
                .isInstanceOf(UserInactiveException.class)
                .hasMessage("Provided user is not active");

        verify(userRepository).findById(MOCK_UUID);
    }

    private User.UserBuilder getTestUserBuilder() {
        return User.builder();
    }
}
