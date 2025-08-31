/* (C) 2025 */
package pl.dealsniper.core.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ValidPasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private Integer minLength;
    private boolean oneCapital;
    private boolean oneDigit;
    private boolean oneSpecialCharacter;
    private static final Pattern INVISIBLE_CHARS_REGEX = Pattern.compile("[\\p{C}\\p{Zs}]");
    private static final Pattern CAPITAL_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[^a-zA-Z0-9].*");

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) {
            return false;
        }
        String sanitized = INVISIBLE_CHARS_REGEX.matcher(password.trim()).replaceAll("");

        return sanitized.length() >= minLength
                && (!oneCapital || CAPITAL_PATTERN.matcher(sanitized).matches())
                && (!oneDigit || DIGIT_PATTERN.matcher(sanitized).matches())
                && (!oneSpecialCharacter
                        || SPECIAL_CHAR_PATTERN.matcher(sanitized).matches());
    }

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.oneCapital = constraintAnnotation.oneCapital();
        this.oneDigit = constraintAnnotation.oneDigit();
        this.oneSpecialCharacter = constraintAnnotation.oneSpecialChar();
    }
}
