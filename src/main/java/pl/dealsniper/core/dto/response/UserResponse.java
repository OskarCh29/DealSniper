package pl.dealsniper.core.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record UserResponse(UUID id, String email, LocalDateTime createdAt) {}
