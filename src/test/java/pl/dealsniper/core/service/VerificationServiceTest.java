/* (C) 2025 */
package pl.dealsniper.core.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.dealsniper.core.mock.MockConst.MOCK_BASE_URL;
import static pl.dealsniper.core.mock.MockConst.MOCK_CREATED_AT;
import static pl.dealsniper.core.mock.MockConst.MOCK_EMAIL;
import static pl.dealsniper.core.mock.MockConst.MOCK_HASH_PASSWORD;
import static pl.dealsniper.core.mock.MockConst.MOCK_VERIFICATION_CODE;
import static pl.dealsniper.core.mock.MockFactory.getMockVerification;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dealsniper.core.configuration.ApplicationConfiguration;
import pl.dealsniper.core.dto.request.user.VerificationRequest;
import pl.dealsniper.core.exception.VerificationCodeException;
import pl.dealsniper.core.model.Verification;
import pl.dealsniper.core.repository.VerificationRepository;
import pl.dealsniper.core.time.TimeProvider;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Mock
    ApplicationConfiguration applicationConfiguration;

    @Mock
    VerificationRepository verificationRepository;

    @Mock
    TimeProvider timeProvider;

    @InjectMocks
    VerificationService underTest;

    @Test
    void generateverificationlink_sucessfull() {
        Verification verification = getMockVerification();
        when(verificationRepository.save(any(), any())).thenReturn(verification);
        when(applicationConfiguration.getBaseUrl()).thenReturn(MOCK_BASE_URL);

        String result = underTest.generateVerificationLink(MOCK_EMAIL);

        assertThat(result).isNotNull().contains(MOCK_BASE_URL);

        verify(verificationRepository).save(any(), any());
        verify(applicationConfiguration).getBaseUrl();
    }

    @Test
    void getEmailByCode_successCodeValid() {
        VerificationRequest request = new VerificationRequest(MOCK_VERIFICATION_CODE, MOCK_HASH_PASSWORD);
        Verification mockVerification = getMockVerification();
        when(verificationRepository.getVerificationByCode(mockVerification.getVerificationCode()))
                .thenReturn(Optional.of(mockVerification));
        when(timeProvider.now()).thenReturn(mockVerification.getCreatedAt());

        String result = underTest.getEmailByCode(request);

        assertThat(result).isEqualTo(mockVerification.getRequestedEmail());
    }

    @Test
    void getEmailByCode_shouldThrowException_whenCodeExpired() {
        VerificationRequest request = new VerificationRequest(MOCK_VERIFICATION_CODE, MOCK_HASH_PASSWORD);
        Verification mockVerification = getMockVerification();
        when(verificationRepository.getVerificationByCode(mockVerification.getVerificationCode()))
                .thenReturn(Optional.of(mockVerification));
        when(timeProvider.now()).thenReturn(MOCK_CREATED_AT.plusDays(1));

        assertThatThrownBy(() -> underTest.getEmailByCode(request))
                .isInstanceOf(VerificationCodeException.class)
                .hasMessage("Verification code expired");
    }

    @Test
    void getEmailByCode_shouldThrowException_whenCodeNotFound() {
        VerificationRequest request = new VerificationRequest(MOCK_VERIFICATION_CODE, MOCK_HASH_PASSWORD);
        Verification mockVerification = getMockVerification();
        when(verificationRepository.getVerificationByCode(mockVerification.getVerificationCode()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getEmailByCode(request))
                .isInstanceOf(VerificationCodeException.class)
                .hasMessage("Invalid verification code");
    }
}
