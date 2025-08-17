package pl.dealsniper.core.dto.response;

import java.util.UUID;

import lombok.Builder;

@Builder
public record SourceResponse(Long id, UUID userId, String filterUrl) {}
