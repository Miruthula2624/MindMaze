package com.mindmaze.common.exception;

/**
 * Thrown when an attempt is made against an inactive, non-existent, or invalid puzzle state.
 */
public class InvalidAttemptException extends MindMazeException {

    public InvalidAttemptException(String message) {
        super(message);
    }

    public InvalidAttemptException(String message, Throwable cause) {
        super(message, cause);
    }
}
