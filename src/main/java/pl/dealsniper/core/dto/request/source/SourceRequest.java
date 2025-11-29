/* (C) 2025 */
package pl.dealsniper.core.dto.request.source;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import pl.dealsniper.core.validation.source.RangeField;
import pl.dealsniper.core.validation.source.RangeFieldDataTypes;

@Builder
@RangeFieldDataTypes({
    @RangeField(min = "minPrice", max = "maxPrice"),
    @RangeField(min = "minYear", max = "maxYear"),
    @RangeField(min = "minMileage", max = "maxMileage")
})
public record SourceRequest(
        @NotNull UUID userId,
        @NotNull(message = "Car brand is missing") String brand,
        String model,
        @Min(0) BigDecimal minPrice,
        BigDecimal maxPrice,
        @Min(1900) Integer minYear,
        Integer maxYear,
        String location,
        @Min(0) Integer minMileage,
        Integer maxMileage,
        boolean damaged,
        TransmissionType transmissionType,
        FuelType fuelType,
        BodyType bodyType) {}
