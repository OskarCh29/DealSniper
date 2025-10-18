/* (C) 2025 */
package pl.dealsniper.core.validation.source;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import pl.dealsniper.core.dto.request.source.BodyType;
import pl.dealsniper.core.dto.request.source.FuelType;
import pl.dealsniper.core.dto.request.source.SourceRequest;
import pl.dealsniper.core.dto.request.source.TransmissionType;

public class RangeFieldTest {

    private final ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);

    private final RangeField annotation = new RangeField() {
        @Override
        public String message() {
            return "Provided {min} or {max} is not in proper range";
        }

        @Override
        public String min() {
            return "minYear";
        }

        @Override
        public String max() {
            return "maxYear";
        }

        @Override
        public Class<?>[] groups() {
            return new Class[0];
        }

        @Override
        public Class<? extends Payload>[] payload() {
            return new Class[0];
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return RangeField.class;
        }
    };

    @ParameterizedTest
    @DisplayName("Proper data range validation test")
    @MethodSource("dataProvider")
    void testRangeValidator(Integer minYear, Integer maxYear, boolean expected) {
        RangeFieldsValidator validator = new RangeFieldsValidator();
        validator.initialize(annotation);
        boolean result = validator.isValid(createTestRequestWithCustomYear(minYear, maxYear), context);
        Assertions.assertEquals(expected, result);
    }

    static Stream<Arguments> dataProvider() {
        return Stream.of(
                Arguments.of(1990, 2025, true),
                Arguments.of(1990, null, true),
                Arguments.of(null, 2025, true),
                Arguments.of(2025, 1990, false));
    }

    private SourceRequest createTestRequestWithCustomYear(Integer minYear, Integer maxYear) {
        return SourceRequest.builder()
                .brand("BMW")
                .model("M3")
                .bodyType(BodyType.COUPE)
                .damaged(false)
                .minYear(minYear)
                .maxYear(maxYear)
                .fuelType(FuelType.PETROL)
                .transmissionType(TransmissionType.MANUAL)
                .minPrice(BigDecimal.valueOf(10000))
                .maxPrice(BigDecimal.valueOf(150000))
                .minMileage(10000)
                .maxMileage(15000)
                .location("Warsaw")
                .build();
    }
}
