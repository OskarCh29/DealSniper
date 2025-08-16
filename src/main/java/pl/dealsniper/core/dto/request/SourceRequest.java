package pl.dealsniper.core.dto.request;

import java.util.UUID;

import lombok.Builder;

@Builder
public record SourceRequest(UUID userId, String filteredUrl) {}
