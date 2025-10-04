/* (C) 2025 */
package pl.dealsniper.core.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PageResponse<T>(List<T> content, int page, int size) {}
