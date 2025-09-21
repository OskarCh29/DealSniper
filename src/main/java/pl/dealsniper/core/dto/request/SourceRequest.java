/* (C) 2025 */
package pl.dealsniper.core.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record SourceRequest(@NotNull UUID userId, @URL String filteredUrl) {}
