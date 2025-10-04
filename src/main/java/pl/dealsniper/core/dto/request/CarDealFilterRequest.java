/* (C) 2025 */
package pl.dealsniper.core.dto.request;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CarDealFilterRequest(
        String title,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minYear,
        Integer maxYear,
        String location,
        Integer minMileage,
        Integer maxMileage) {}
