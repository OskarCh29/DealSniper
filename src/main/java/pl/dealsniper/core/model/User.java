/* (C) 2025 */
package pl.dealsniper.core.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {

    private UUID id;

    private String email;

    private String password;

    private LocalDateTime createdAt;

    private Boolean active;

    private LocalDateTime deletedAt;
}
