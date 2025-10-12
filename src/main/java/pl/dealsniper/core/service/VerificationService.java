/* (C) 2025 */
package pl.dealsniper.core.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.configuration.ApplicationProperties;
import pl.dealsniper.core.dto.request.user.VerificationRequest;
import pl.dealsniper.core.exception.VerificationCodeException;
import pl.dealsniper.core.model.Verification;
import pl.dealsniper.core.repository.VerificationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final ApplicationProperties applicationProperties;
    private final VerificationRepository verificationRepository;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Integer CODE_BYTE_LENGTH = 32;
    private static final LocalDateTime EXPIRY_TIME = LocalDateTime.now().minusHours(1);

    @Transactional
    public String generateVerificationLink(String email) {
        String generatedCode = generateVerificationCode();
        verificationRepository.save(generatedCode, email);
        return applicationProperties.getBaseUrl() + "/verification.html?code=" + generatedCode;
    }

    public Verification validateCode(String code) {
        Verification verification = verificationRepository
                .getVerificationByCode(code)
                .orElseThrow(() -> new VerificationCodeException("Invalid verification code"));

        if (EXPIRY_TIME.isAfter(verification.getCreatedAt())) {
            throw new VerificationCodeException("Verification code expired");
        }
        return verification;
    }

    public String getEmailByCode(VerificationRequest verificationRequest) {
        Verification verification = validateCode(verificationRequest.code());
        return verification.getRequestedEmail();
    }

    @Transactional
    public void deleteUsedVerification(String code) {
        verificationRepository.deleteVerificationByCode(code);
    }

    private String generateVerificationCode() {
        byte[] randomBytes = new byte[CODE_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        return DigestUtils.sha256Hex(randomBytes);
    }
}
