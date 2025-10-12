/* (C) 2025 */
package pl.dealsniper.core.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
import pl.dealsniper.core.dto.request.source.SourceRequest;
import pl.dealsniper.core.exception.RecordNotFoundException;
import pl.dealsniper.core.mapper.SourceMapper;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.SourceRepository;
import pl.dealsniper.core.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SourceServiceTest {

    private static final UUID TEST_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String TEST_URL = "https://testUrl.com";
    private static final UUID TEST_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final LocalDateTime TEST_CREATE_DATE = LocalDateTime.of(2025, 3, 15, 10, 0);
    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_HASHED_PASSWORD =
            "b56094a66443430e2b0871f31439fda21d7c4a50cd5ddadb511c85114e3906c9";

    @Mock
    private SourceRepository sourceRepository;

    @Spy
    private SourceMapper sourceMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SourceService sourceService;

    @Test
    void saveUserSourceShouldSaveWhenUserExists() {
        SourceRequest request = getSampleSourceRequest();
        doAnswer(invocation -> {
                    SourceRequest req = invocation.getArgument(0);
                    return Source.builder()
                            .userId(req.userId())
                            .filteredUrl(req.filteredUrl())
                            .build();
                })
                .when(sourceMapper)
                .toDomainSource(any(SourceRequest.class));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.ofNullable(getUserWithSampleData()));
        when(sourceRepository.save(any(Source.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Source savedSource = sourceService.saveUserSource(request);

        assertThat(savedSource)
                .extracting(Source::getUserId, Source::getFilteredUrl)
                .containsExactly(TEST_USER_ID, TEST_URL);

        verify(userRepository).findById(TEST_USER_ID);
        verify(sourceRepository).save(any(Source.class));
    }

    @Test
    void saveUserSourceShouldThrowExceptionWhenUserNotExists() {
        SourceRequest request = getSampleSourceRequest();
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sourceService.saveUserSource(request))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("User with provided Id not found");
    }

    private SourceRequest getSampleSourceRequest() {
        return SourceRequest.builder()
                .userId(TEST_USER_ID)
                .filteredUrl(TEST_URL)
                .build();
    }

    private static User getUserWithSampleData() {
        return User.builder()
                .id(TEST_UUID)
                .email(TEST_EMAIL)
                .password(TEST_HASHED_PASSWORD)
                .active(true)
                .createdAt(TEST_CREATE_DATE)
                .build();
    }

    private Source.SourceBuilder getSourceBuilder() {
        return Source.builder();
    }
}
