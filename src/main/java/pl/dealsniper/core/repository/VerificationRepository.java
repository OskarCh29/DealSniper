/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.Optional;
import pl.dealsniper.core.model.Verification;

public interface VerificationRepository {

    Verification save(String verificationCode, String email);

    Optional<Verification> getVerificationByCode(String code);

    void deleteVerificationByCode(String code);

    void deactivateExpiredCodes();
}
