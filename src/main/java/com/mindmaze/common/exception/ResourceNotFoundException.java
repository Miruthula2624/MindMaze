package com.mindmaze.common.exception;

/**
 * Thrown when a requested resource (such as a category or puzzle) cannot be located.
 */
public class ResourceNotFoundException extends MindMazeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
