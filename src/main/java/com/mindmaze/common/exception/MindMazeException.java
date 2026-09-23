package com.mindmaze.common.exception;

/**
 * Root unchecked exception for all MindMaze domain exceptions.
 */
public class MindMazeException extends RuntimeException {

    public MindMazeException(String message) {
        super(message);
    }

    public MindMazeException(String message, Throwable cause) {
        super(message, cause);
    }
}
