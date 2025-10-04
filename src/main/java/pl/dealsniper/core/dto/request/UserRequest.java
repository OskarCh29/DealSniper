/* (C) 2025 */
package pl.dealsniper.core.dto.request;

import lombok.Builder;

@Builder
public record UserRequest(String email, String password) {}
