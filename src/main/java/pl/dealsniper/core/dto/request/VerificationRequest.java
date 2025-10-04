/* (C) 2025 */
package pl.dealsniper.core.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.dealsniper.core.validation.user.ValidPassword;

public record VerificationRequest(
        @NotNull String code,
        @ValidPassword(minLength = 8, oneCapital = true, oneDigit = true, oneSpecialChar = true) String password) {}
