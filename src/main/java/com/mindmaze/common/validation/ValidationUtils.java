package com.mindmaze.common.validation;

import com.mindmaze.common.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Pure Java validation utility methods for domain invariant enforcement.
 */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,20}$"
    );

    private ValidationUtils() {
        // Utility class
    }

    public static void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username.trim()).matches()) {
            throw new ValidationException("Username must be between 3 and 20 characters and contain only alphanumeric characters and underscores.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public static void validateNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be null or blank.");
        }
    }

    public static void validateTimeSpent(long timeSpentMs) {
        if (timeSpentMs < 500) {
            throw new ValidationException("Time spent (" + timeSpentMs + "ms) must be at least 500ms.");
        }
        if (timeSpentMs > 600_000) {
            throw new ValidationException("Time spent (" + timeSpentMs + "ms) exceeds reasonable puzzle time limit of 10 minutes.");
        }
    }
}
