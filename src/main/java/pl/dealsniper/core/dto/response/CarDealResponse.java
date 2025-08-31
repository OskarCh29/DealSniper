/* (C) 2025 */
package pl.dealsniper.core.dto.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CarDealResponse(
        String title,
        BigDecimal price,
        String currency,
        String offerUrl,
        String location,
        String mileage,
        Integer year) {}
