/* (C) 2025 */
package pl.dealsniper.core.dto.request.cardeal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CarDealFilterRequest(
        String title,
        @Min(0) BigDecimal minPrice,
        @Min(0) BigDecimal maxPrice,
        @Min(1900) Integer minYear,
        @Positive Integer maxYear,
        String location,
        @Min(0) Integer minMileage,
        @Positive Integer maxMileage) {}
