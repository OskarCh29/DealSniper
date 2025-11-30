/* (C) 2025 */
package pl.dealsniper.core.util;

import java.util.function.Supplier;

public class ValidationUtil {

    private ValidationUtil() {}

    public static void throwIfTrue(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition) {
            throw exceptionSupplier.get();
        }
    }

    public static void throwIfFalse(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!condition) {
            throw exceptionSupplier.get();
        }
    }
}
