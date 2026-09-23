package com.mindmaze.common.exception;

/**
 * Thrown when domain validation rules or invariants are violated.
 */
public class ValidationException extends MindMazeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
