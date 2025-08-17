package pl.dealsniper.core.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ErrorResponse(int status, String statusMessage, String message, String path, LocalDateTime timeStamp) {}
