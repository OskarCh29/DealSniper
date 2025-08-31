/* (C) 2025 */
package pl.dealsniper.core.validation.user;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Annotation;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class ValidPasswordTest {

    private ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);

    private ValidPassword annotation = new ValidPassword() {

        @Override
        public int minLength() {
            return 8;
        }

        @Override
        public boolean oneCapital() {
            return true;
        }

        @Override
        public boolean oneDigit() {
            return true;
        }

        @Override
        public boolean oneSpecialChar() {
            return true;
        }

        @Override
        public String message() {
            return "Invalid password";
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
            return ValidPassword.class;
        }
    };

    @ParameterizedTest
    @MethodSource("passwordProvider")
    void testValidPassword(String password, boolean expectedValidation) {
        ValidPasswordConstraintValidator validator = new ValidPasswordConstraintValidator();
        validator.initialize(annotation);
        boolean valid = validator.isValid(password, context);
        Assertions.assertEquals(expectedValidation, valid);
    }

    static Stream<Arguments> passwordProvider() {
        return Stream.of(
                Arguments.of("Abc1234!", true),
                Arguments.of("Abc12345", false),
                Arguments.of("abc1234!", false),
                Arguments.of("AbcAbcd!", false),
                Arguments.of("Ab2!", false));
    }
}
