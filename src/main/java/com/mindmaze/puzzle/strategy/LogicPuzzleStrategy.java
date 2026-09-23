package com.mindmaze.puzzle.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;

/**
 * Concrete strategy for LOGIC cognitive puzzles (deductive reasoning, riddles, conditional selection).
 */
public class LogicPuzzleStrategy extends AbstractPuzzleStrategy {

    public LogicPuzzleStrategy() {
        super();
    }

    public LogicPuzzleStrategy(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public PuzzleType getSupportedType() {
        return PuzzleType.LOGIC;
    }

    @Override
    public boolean validateAnswer(Puzzle puzzle, String userSubmissionJson) {
        JsonNode solutionNode = parseJson(puzzle.getSolutionDataJson());
        JsonNode submissionNode = parseJson(userSubmissionJson);

        String expectedOption = extractStringValue(solutionNode, "correctOption", "option", "answer");
        String submittedOption = extractStringValue(submissionNode, "selectedOption", "option", "answer");

        return expectedOption.equalsIgnoreCase(submittedOption);
    }

    private String extractStringValue(JsonNode node, String... fieldCandidates) {
        for (String field : fieldCandidates) {
            if (node.has(field)) {
                return node.get(field).asText().trim();
            }
        }
        throw new IllegalArgumentException("No valid option field found in JSON node. Expected one of: "
                + String.join(", ", fieldCandidates));
    }
}
