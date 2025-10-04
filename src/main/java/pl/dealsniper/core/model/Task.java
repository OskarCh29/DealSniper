/* (C) 2025 */
package pl.dealsniper.core.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Task {

    private UUID userId;

    private Long sourceId;

    private String taskName;
}
