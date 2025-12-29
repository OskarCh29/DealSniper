/* (C) 2025 */
package pl.dealsniper.core.dto.response.source;

import java.util.UUID;
import lombok.Builder;

@Builder
public record SourceResponse(Long id, UUID userId, String filteredUrl) {}
