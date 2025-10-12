/* (C) 2025 */
package pl.dealsniper.core.dto.request.source;

import java.math.BigDecimal;

public record UrlSourceRequest(
        String brand,
        String model,
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
