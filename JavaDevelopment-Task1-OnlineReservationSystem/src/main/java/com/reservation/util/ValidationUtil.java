
package com.reservation.util;

public final class ValidationUtil {

    // Prevent object creation.
    private ValidationUtil() {
    }

    // Check whether a string is null, empty, or contains only whitespace.
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
