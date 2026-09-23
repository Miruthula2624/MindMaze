package com.mindmaze.puzzle.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmaze.common.exception.InvalidAttemptException;
import com.mindmaze.common.exception.ValidationException;
import com.mindmaze.puzzle.domain.Puzzle;

/**
 * Abstract base class providing common JSON parsing, input validation, and evaluation scaffolding
 * for all category-specific puzzle strategies.
 */
public abstract class AbstractPuzzleStrategy implements PuzzleStrategy {

    protected final ObjectMapper objectMapper;

    protected AbstractPuzzleStrategy() {
        this.objectMapper = new ObjectMapper();
    }

    protected AbstractPuzzleStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    @Override
    public EvaluationResult evaluate(Puzzle puzzle, String userSubmissionJson, long timeSpentMs) {
        validateSubmissionInput(puzzle, userSubmissionJson, timeSpentMs);

        boolean isCorrect = validateAnswer(puzzle, userSubmissionJson);
        String feedback = generateFeedback(isCorrect, timeSpentMs, puzzle.getTargetTimeSeconds());
        String sanitizedSubmission = sanitizeSubmissionString(userSubmissionJson);

        return new EvaluationResult(
                isCorrect,
                timeSpentMs,
                puzzle.getTargetTimeSeconds(),
                puzzle.getDifficultyTier(),
                puzzle.getCategoryId(),
                feedback,
                sanitizedSubmission
        );
    }

    /**
     * Validates runtime invariants before answer validation executes.
     */
    protected void validateSubmissionInput(Puzzle puzzle, String userSubmissionJson, long timeSpentMs) {
        if (puzzle == null) {
            throw new ValidationException("Puzzle cannot be null for evaluation.");
        }
        if (!puzzle.isActive()) {
            throw new InvalidAttemptException("Cannot evaluate an inactive puzzle.");
        }
        if (puzzle.getPuzzleType() != getSupportedType()) {
            throw new ValidationException("Strategy type mismatch. Expected: "
                    + getSupportedType() + ", but puzzle was: " + puzzle.getPuzzleType());
        }
        if (userSubmissionJson == null || userSubmissionJson.trim().isEmpty()) {
            throw new ValidationException("User submission payload cannot be empty.");
        }
        if (timeSpentMs < 500) {
            throw new ValidationException("Time spent must be at least 500ms to reflect valid human interaction.");
        }
    }

    /**
     * Generates a constructive feedback message based on performance.
     */
    protected String generateFeedback(boolean isCorrect, long timeSpentMs, int targetTimeSeconds) {
        if (!isCorrect) {
            return "Incorrect solution. Review the challenge patterns and try again.";
        }
        long timeInSeconds = timeSpentMs / 1000;
        if (timeInSeconds <= targetTimeSeconds / 2) {
            return "Exceptional solve! Lightning fast reaction time.";
        } else if (timeInSeconds <= targetTimeSeconds) {
            return "Great job! Successfully solved within target time.";
        } else {
            return "Correct solve! Focus on speeding up your reaction pace next time.";
        }
    }

    /**
     * Helper to parse a JSON string into a Jackson JsonNode.
     */
    protected JsonNode parseJson(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new ValidationException("Malformed JSON payload: " + e.getMessage(), e);
        }
    }

    /**
     * Truncates or cleans submission string for logging and storage.
     */
    protected String sanitizeSubmissionString(String userSubmissionJson) {
        if (userSubmissionJson == null) {
            return "";
        }
        String trimmed = userSubmissionJson.trim();
        return trimmed.length() > 500 ? trimmed.substring(0, 500) + "..." : trimmed;
    }
}
