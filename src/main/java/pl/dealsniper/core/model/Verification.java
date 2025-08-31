/* (C) 2025 */
package pl.dealsniper.core.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Verification {

    private String email;

    private String verificationCode;

    private LocalDateTime createdAt;
}
