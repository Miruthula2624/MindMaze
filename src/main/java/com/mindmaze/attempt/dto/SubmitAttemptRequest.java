package com.mindmaze.attempt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for submitting an answer attempt to a cognitive puzzle.
 */
public record SubmitAttemptRequest(
        @NotNull(message = "Puzzle ID cannot be null.")
        Long puzzleId,

        @NotBlank(message = "User submission JSON payload cannot be blank.")
        String userSubmissionJson,

        @Min(value = 500, message = "Time spent must be at least 500 milliseconds.")
        long timeSpentMs,

        @Min(value = 0, message = "Session consecutive streak cannot be negative.")
        int sessionConsecutiveStreak
) {
}
