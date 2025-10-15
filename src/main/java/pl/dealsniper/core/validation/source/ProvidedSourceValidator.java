/* (C) 2025 */
package pl.dealsniper.core.validation.source;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProvidedSourceValidator implements ConstraintValidator<ProvidedSource, UserSource> {

    @Override
    public boolean isValid(UserSource userSource, ConstraintValidatorContext constraintValidatorContext) {
        if (userSource.filteredUrl() == null && userSource.urlRequest() == null) {
            return false;
        }
        return userSource.filteredUrl() == null || userSource.urlRequest() == null;
    }

    @Override
    public void initialize(ProvidedSource constraintAnnotation) {}
}
