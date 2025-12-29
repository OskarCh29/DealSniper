/* (C) 2025 */
package pl.dealsniper.core.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.dealsniper.core.mock.MockConst.MOCK_CREATED_AT;
import static pl.dealsniper.core.mock.MockConst.MOCK_EMAIL;
import static pl.dealsniper.core.mock.MockConst.MOCK_HASH_PASSWORD;
import static pl.dealsniper.core.mock.MockConst.MOCK_ID;
import static pl.dealsniper.core.mock.MockConst.MOCK_OFFER_URL;
import static pl.dealsniper.core.mock.MockConst.MOCK_UUID;
import static pl.dealsniper.core.mock.MockFactory.getMockSource;
import static pl.dealsniper.core.mock.MockFactory.getMockUser;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dealsniper.core.dto.response.source.SourceResponse;
import pl.dealsniper.core.exception.db.RecordNotFoundException;
import pl.dealsniper.core.exception.db.ResourceUsedException;
import pl.dealsniper.core.exception.user.UserInactiveException;
import pl.dealsniper.core.mapper.SourceMapper;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.SourceRepository;
import pl.dealsniper.core.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SourceServiceTest {

    @Mock
    SourceRepository sourceRepository;

    @Spy
    SourceMapper sourceMapper;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    SourceService underTest;

    @Test
    void saveUserSource_shouldSave_whenUserActive_andSourceNotExists() {
        User user = getMockUser();
        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.of(user));

        Source mockSource = getMockSource();
        when(sourceRepository.save(any())).thenReturn(mockSource);

        Source result = underTest.saveUserSource(MOCK_UUID, MOCK_OFFER_URL);

        assertThat(result).isEqualTo(mockSource);

        verify(sourceRepository).save(any(Source.class));
    }

    @Test
    void saveUserSource_shouldThrow_whenSourceAlreadyExists() {
        User user = getMockUser();
        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.saveUserSource(MOCK_UUID, MOCK_OFFER_URL))
                .isInstanceOf(ResourceUsedException.class)
                .hasMessage("Provided source url already exists on your account");
    }

    @Test
    void saveUserSource_shouldThrow_whenUserInactive() {
        User user = User.builder()
                .id(MOCK_UUID)
                .email(MOCK_EMAIL)
                .password(MOCK_HASH_PASSWORD)
                .createdAt(MOCK_CREATED_AT)
                .active(false)
                .build();

        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.saveUserSource(MOCK_UUID, MOCK_OFFER_URL))
                .isInstanceOf(UserInactiveException.class)
                .hasMessage("Provided user is not active");
    }

    @Test
    void saveUserSource_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(MOCK_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.saveUserSource(MOCK_UUID, MOCK_OFFER_URL))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("User with provided Id not found");
    }

    @Test
    void deleteSourceById_shouldNotThrow_whenDeleted() {
        when(sourceRepository.deleteById(MOCK_ID)).thenReturn(1);

        assertThatCode(() -> underTest.deleteSourceById(MOCK_ID)).doesNotThrowAnyException();
    }

    @Test
    void deleteSourceById_shouldThrow_whenNothingDeleted() {
        when(sourceRepository.deleteById(MOCK_ID)).thenReturn(0);

        assertThatThrownBy(() -> underTest.deleteSourceById(MOCK_ID)).isInstanceOf(RecordNotFoundException.class);
    }

    @Test
    void validateUserOwnsSource_shouldNotThrow_whenExists() {
        when(sourceRepository.existsForUserAndActive(MOCK_UUID, MOCK_ID)).thenReturn(true);

        assertThatCode(() -> underTest.validateUserOwnsSource(MOCK_UUID, MOCK_ID))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUserOwnsSource_shouldThrow_whenNotExists() {
        when(sourceRepository.existsForUserAndActive(MOCK_UUID, MOCK_ID)).thenReturn(false);

        assertThatThrownBy(() -> underTest.validateUserOwnsSource(MOCK_UUID, MOCK_ID))
                .isInstanceOf(RecordNotFoundException.class);
    }

    @Test
    void getSourceById_shouldReturn_whenExists() {
        Source source = getMockSource();
        when(sourceRepository.findById(MOCK_ID)).thenReturn(Optional.of(source));

        Source result = underTest.getSourceById(MOCK_ID);

        assertThat(result).isEqualTo(source);
    }

    @Test
    void getSourceById_shouldThrow_whenNotFound() {
        when(sourceRepository.findById(MOCK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getSourceById(MOCK_ID)).isInstanceOf(RecordNotFoundException.class);
    }

    @Test
    void getSourceByIdAndUserId_shouldReturn_whenMatchesUser() {
        Source source = getMockSource();
        when(sourceRepository.findByIdAndUserId(MOCK_ID, MOCK_UUID)).thenReturn(Optional.of(source));

        Source result = underTest.getSourceByIdAndUserId(MOCK_ID, MOCK_UUID);

        assertThat(result).isEqualTo(source);
    }

    @Test
    void getSourceByIdAndUserId_shouldThrow_whenNotFound() {
        when(sourceRepository.findByIdAndUserId(MOCK_ID, MOCK_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getSourceByIdAndUserId(MOCK_ID, MOCK_UUID))
                .isInstanceOf(RecordNotFoundException.class);
    }

    @Test
    void findSourceById_shouldReturn_whenExists() {
        Source source = getMockSource();
        SourceResponse response = new SourceResponse(MOCK_ID, MOCK_UUID, MOCK_OFFER_URL);

        when(sourceRepository.findById(MOCK_ID)).thenReturn(Optional.of(source));
        when(sourceMapper.toSourceResponse(source)).thenReturn(response);

        SourceResponse result = underTest.findSourceById(MOCK_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findSourceById_shouldThrow_whenNotFound() {
        when(sourceRepository.findById(MOCK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findSourceById(MOCK_ID)).isInstanceOf(RecordNotFoundException.class);
    }
}
