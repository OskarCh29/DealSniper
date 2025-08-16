package pl.dealsniper.core.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import pl.dealsniper.core.validation.user.PasswordRequest;
import pl.dealsniper.core.validation.user.ValidPassword;

@Builder
public record UserRequest(
        @Email String email,
        @ValidPassword(minLength = 8, oneCapital = true, oneDigit = true, oneSpecialChar = true) String password)
        implements PasswordRequest {}
