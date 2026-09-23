package com.mindmaze.puzzle.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete strategy for WORD cognitive puzzles (anagrams, word ladders, lexical unscrambling).
 */
public class WordPuzzleStrategy extends AbstractPuzzleStrategy {

    public WordPuzzleStrategy() {
        super();
    }

    public WordPuzzleStrategy(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public PuzzleType getSupportedType() {
        return PuzzleType.WORD;
    }

    @Override
    public boolean validateAnswer(Puzzle puzzle, String userSubmissionJson) {
        JsonNode solutionNode = parseJson(puzzle.getSolutionDataJson());
        JsonNode submissionNode = parseJson(userSubmissionJson);

        String submittedWord = extractSubmittedWord(submissionNode).trim().toLowerCase();
        Set<String> acceptedWords = extractAcceptedWords(solutionNode);

        return acceptedWords.contains(submittedWord);
    }

    private String extractSubmittedWord(JsonNode node) {
        String[] candidates = {"word", "answer", "submittedWord", "text"};
        for (String candidate : candidates) {
            if (node.has(candidate)) {
                return node.get(candidate).asText();
            }
        }
        throw new IllegalArgumentException("No word submission found. Expected one of: word, answer, submittedWord");
    }

    private Set<String> extractAcceptedWords(JsonNode node) {
        Set<String> words = new HashSet<>();
        if (node.has("targetWord")) {
            words.add(node.get("targetWord").asText().trim().toLowerCase());
        }
        if (node.has("acceptedWords") && node.get("acceptedWords").isArray()) {
            for (JsonNode item : node.get("acceptedWords")) {
                words.add(item.asText().trim().toLowerCase());
            }
        }
        if (words.isEmpty() && node.has("answer")) {
            words.add(node.get("answer").asText().trim().toLowerCase());
        }
        if (words.isEmpty()) {
            throw new IllegalArgumentException("No accepted words defined in solution data.");
        }
        return words;
    }
}
