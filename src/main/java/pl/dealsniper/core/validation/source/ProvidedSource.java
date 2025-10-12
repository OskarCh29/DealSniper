/* (C) 2025 */
package pl.dealsniper.core.validation.source;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD, ElementType.TYPE})
@Constraint(validatedBy = ProvidedSourceValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProvidedSource {

    String message() default "One source type is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
