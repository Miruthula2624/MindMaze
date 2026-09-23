package com.mindmaze.puzzle.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;

/**
 * Concrete strategy for PATTERN cognitive puzzles (matrix reasoning, abstract visual sequence completion).
 */
public class PatternPuzzleStrategy extends AbstractPuzzleStrategy {

    public PatternPuzzleStrategy() {
        super();
    }

    public PatternPuzzleStrategy(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public PuzzleType getSupportedType() {
        return PuzzleType.PATTERN;
    }

    @Override
    public boolean validateAnswer(Puzzle puzzle, String userSubmissionJson) {
        JsonNode solutionNode = parseJson(puzzle.getSolutionDataJson());
        JsonNode submissionNode = parseJson(userSubmissionJson);

        String expectedPattern = extractPatternId(solutionNode, "correctPatternId", "patternId", "answer");
        String submittedPattern = extractPatternId(submissionNode, "selectedPatternId", "patternId", "answer");

        return expectedPattern.equalsIgnoreCase(submittedPattern);
    }

    private String extractPatternId(JsonNode node, String... fieldCandidates) {
        for (String field : fieldCandidates) {
            if (node.has(field)) {
                return node.get(field).asText().trim();
            }
        }
        throw new IllegalArgumentException("No valid pattern identifier found in JSON node. Expected one of: "
                + String.join(", ", fieldCandidates));
    }
}
