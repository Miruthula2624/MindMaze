package com.mindmaze.puzzle.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Concrete strategy for MEMORY cognitive puzzles (spatial tile recall, sequence replication).
 */
public class MemoryPuzzleStrategy extends AbstractPuzzleStrategy {

    public MemoryPuzzleStrategy() {
        super();
    }

    public MemoryPuzzleStrategy(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public PuzzleType getSupportedType() {
        return PuzzleType.MEMORY;
    }

    @Override
    public boolean validateAnswer(Puzzle puzzle, String userSubmissionJson) {
        JsonNode solutionNode = parseJson(puzzle.getSolutionDataJson());
        JsonNode submissionNode = parseJson(userSubmissionJson);

        // Check if strict sequence order is required (e.g., "ordered": true, default is true for sequences)
        boolean isOrdered = solutionNode.has("ordered") ? solutionNode.get("ordered").asBoolean() : true;

        JsonNode expectedArray = extractArrayNode(solutionNode, "sequence", "tiles", "pattern");
        JsonNode submittedArray = extractArrayNode(submissionNode, "sequence", "tiles", "pattern");

        if (expectedArray.size() != submittedArray.size()) {
            return false;
        }

        if (isOrdered) {
            List<String> expectedList = jsonArrayToStringList(expectedArray);
            List<String> submittedList = jsonArrayToStringList(submittedArray);
            return expectedList.equals(submittedList);
        } else {
            Set<String> expectedSet = jsonArrayToStringSet(expectedArray);
            Set<String> submittedSet = jsonArrayToStringSet(submittedArray);
            return expectedSet.equals(submittedSet);
        }
    }

    private JsonNode extractArrayNode(JsonNode node, String... fieldCandidates) {
        for (String field : fieldCandidates) {
            if (node.has(field) && node.get(field).isArray()) {
                return node.get(field);
            }
        }
        throw new IllegalArgumentException("No valid array field found in JSON node. Expected one of: "
                + String.join(", ", fieldCandidates));
    }

    private List<String> jsonArrayToStringList(JsonNode arrayNode) {
        List<String> list = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            list.add(item.asText().trim().toLowerCase());
        }
        return list;
    }

    private Set<String> jsonArrayToStringSet(JsonNode arrayNode) {
        Set<String> set = new HashSet<>();
        for (JsonNode item : arrayNode) {
            set.add(item.asText().trim().toLowerCase());
        }
        return set;
    }
}
