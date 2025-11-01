/* (C) 2025 */
package pl.dealsniper.core.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Verification {

    private String verificationCode;

    private LocalDateTime createdAt;

    private String requestedEmail;
}
