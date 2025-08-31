/* (C) 2025 */
package pl.dealsniper.core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AccountRequest(@NotNull @Email String email) {}
