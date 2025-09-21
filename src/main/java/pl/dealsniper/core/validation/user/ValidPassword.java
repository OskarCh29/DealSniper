/* (C) 2025 */
package pl.dealsniper.core.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPasswordConstraintValidator.class)
public @interface ValidPassword {

    String message() default "Password not meets requirements";

    int minLength() default 6;

    boolean oneCapital() default false;

    boolean oneDigit() default false;

    boolean oneSpecialChar() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
