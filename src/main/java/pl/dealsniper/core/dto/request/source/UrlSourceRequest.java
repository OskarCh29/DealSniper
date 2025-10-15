/* (C) 2025 */
package pl.dealsniper.core.dto.request.source;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UrlSourceRequest(
        @NotNull(message = "Car brand is missing") String brand,
        @NotNull(message = "Car model is missing") String model,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minYear,
        Integer maxYear,
        String location,
        Integer minMileage,
        Integer maxMileage,
        boolean damaged,
        TransmissionType transmissionType,
        FuelType fuelType,
        BodyType bodyType) {}
