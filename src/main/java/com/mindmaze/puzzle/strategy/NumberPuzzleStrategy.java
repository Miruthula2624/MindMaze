package com.mindmaze.puzzle.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;

/**
 * Concrete strategy for NUMBER cognitive puzzles (mental arithmetic, number sequences, equation balancing).
 */
public class NumberPuzzleStrategy extends AbstractPuzzleStrategy {

    public NumberPuzzleStrategy() {
        super();
    }

    public NumberPuzzleStrategy(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public PuzzleType getSupportedType() {
        return PuzzleType.NUMBER;
    }

    @Override
    public boolean validateAnswer(Puzzle puzzle, String userSubmissionJson) {
        JsonNode solutionNode = parseJson(puzzle.getSolutionDataJson());
        JsonNode submissionNode = parseJson(userSubmissionJson);

        // Solution can be specified as "expectedValue", "value", or "correctAnswer"
        double expectedValue = extractNumericValue(solutionNode, "expectedValue", "value", "correctAnswer");
        double submittedValue = extractNumericValue(submissionNode, "submittedValue", "value", "answer");

        // Use epsilon comparison for floating-point tolerance
        return Math.abs(expectedValue - submittedValue) < 0.0001;
    }

    private double extractNumericValue(JsonNode node, String... fieldCandidates) {
        for (String field : fieldCandidates) {
            if (node.has(field)) {
                JsonNode valNode = node.get(field);
                if (valNode.isNumber()) {
                    return valNode.asDouble();
                } else if (valNode.isTextual()) {
                    try {
                        return Double.parseDouble(valNode.asText().trim());
                    } catch (NumberFormatException ignored) {
                        // Continue checking or throw
                    }
                }
            }
        }
        throw new IllegalArgumentException("No valid numeric field found in JSON node. Expected one of: "
                + String.join(", ", fieldCandidates));
    }
}
