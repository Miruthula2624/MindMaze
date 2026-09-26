package com.mindmaze.common.error;

import com.mindmaze.common.exception.InvalidAttemptException;
import com.mindmaze.common.exception.ResourceNotFoundException;
import com.mindmaze.common.exception.StrategyNotFoundException;
import com.mindmaze.common.exception.ValidationException;
import com.mindmaze.puzzle.domain.PuzzleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should translate ResourceNotFoundException to 404 ProblemDetail")
    void shouldHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Category not found");
        ProblemDetail problem = handler.handleResourceNotFound(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Resource Not Found");
        assertThat(problem.getDetail()).isEqualTo("Category not found");
        assertThat(problem.getProperties()).containsEntry("errorCode", "RESOURCE_NOT_FOUND");
    }

    @Test
    @DisplayName("Should translate StrategyNotFoundException to 404 ProblemDetail")
    void shouldHandleStrategyNotFound() {
        StrategyNotFoundException ex = new StrategyNotFoundException(PuzzleType.NUMBER);
        ProblemDetail problem = handler.handleStrategyNotFound(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Strategy Not Found");
        assertThat(problem.getProperties()).containsEntry("errorCode", "STRATEGY_NOT_FOUND");
    }

    @Test
    @DisplayName("Should translate ValidationException to 400 ProblemDetail")
    void shouldHandleValidationException() {
        ValidationException ex = new ValidationException("Invalid username format");
        ProblemDetail problem = handler.handleValidationException(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Validation Error");
        assertThat(problem.getProperties()).containsEntry("errorCode", "VALIDATION_ERROR");
    }

    @Test
    @DisplayName("Should translate InvalidAttemptException to 400 ProblemDetail")
    void shouldHandleInvalidAttempt() {
        InvalidAttemptException ex = new InvalidAttemptException("Cannot attempt inactive puzzle");
        ProblemDetail problem = handler.handleInvalidAttempt(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Invalid Attempt");
        assertThat(problem.getProperties()).containsEntry("errorCode", "INVALID_ATTEMPT");
    }

    @Test
    @DisplayName("Should translate generic Exception to 500 ProblemDetail with generic message")
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected database timeout");
        ProblemDetail problem = handler.handleGenericException(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getDetail()).doesNotContain("database timeout"); // Stack trace masked
        assertThat(problem.getProperties()).containsEntry("errorCode", "INTERNAL_SERVER_ERROR");
    }
}
