package pl.dealsniper.core.dto.response;

import java.math.BigDecimal;

public record CarDealResponse(
        String title,

        BigDecimal price,

        String currency,

        String offerUrl,

        String location,

        String mileage,

        Integer year
) {
}
