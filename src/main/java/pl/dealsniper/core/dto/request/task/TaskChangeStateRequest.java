/* (C) 2025 */
package pl.dealsniper.core.dto.request.task;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import pl.dealsniper.core.validation.Required;

public record TaskChangeStateRequest(
        @NotNull UUID userId, @NotNull Long sourceId, @Required String taskName, @NotNull TaskState state) {}
