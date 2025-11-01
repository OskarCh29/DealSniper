/* (C) 2025 */
package pl.dealsniper.core.validation.source;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

public class RangeFieldsValidator implements ConstraintValidator<RangeField, Object> {

    private String minField;
    private String maxField;
    private String message;

    @Override
    public void initialize(RangeField constraintAnnotation) {
        this.maxField = constraintAnnotation.max();
        this.minField = constraintAnnotation.min();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return false;
        }
        try {
            Object minValue = getFieldValue(object, minField);
            Object maxValue = getFieldValue(object, maxField);
            if (minValue == null || maxValue == null) {
                return true;
            }
            if (!(minValue instanceof Comparable) || !(maxValue instanceof Comparable)) {
                getDetailedMessage(minField, maxField, context);
                return false;
            }

            if ((((Comparable) minValue).compareTo(maxValue)) > 0) {
                getDetailedMessage(minField, maxField, context);
                return false;
            }

        } catch (Exception e) {
            this.message = "Provided fields are not applicable. Check if they exist";
            return false;
        }
        return true;
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Method method = object.getClass().getMethod(fieldName);
        return method.invoke(object);
    }

    private void getDetailedMessage(String minField, String maxField, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        message.replace("{min}", minField).replace("{max}", maxField))
                .addPropertyNode(minField)
                .addConstraintViolation();
    }
}
