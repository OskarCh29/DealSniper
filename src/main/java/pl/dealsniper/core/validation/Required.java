/* (C) 2025 */
package pl.dealsniper.core.validation;

import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@NotBlank
@NotNull
@ReportAsSingleViolation
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
    String message() default "Required field is missing";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
