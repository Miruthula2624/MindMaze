package com.mindmaze.puzzle.catalog;

import com.mindmaze.puzzle.domain.Category;
import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary in-memory development catalog for Phase 2.
 * <p>
 * ARCHITECTURE NOTICE:
 * This component serves as an isolated Phase 2 in-memory adapter because databases, JDBC, and JPA
 * are strictly excluded from Phase 2. In Phase 3, this adapter will be replaced by database repositories.
 */
@Component
public class InMemoryPuzzleCatalog {

    private final Map<Long, Category> categoryMap = new ConcurrentHashMap<>();
    private final Map<Long, Puzzle> puzzleMap = new ConcurrentHashMap<>();

    public InMemoryPuzzleCatalog() {
        seedInitialData();
    }

    private void seedInitialData() {
        // 1. Categories
        Category numberCategory = new Category(1L, "NUMBER", "Number Reasoning", "Mental math, sequences, and equation balance", "calculator");
        Category logicCategory = new Category(2L, "LOGIC", "Logical Deduction", "Deductive riddles and logical constraints", "brain");
        Category memoryCategory = new Category(3L, "MEMORY", "Working Memory", "Sequence recall and visual spatial memory", "lightbulb");
        Category wordCategory = new Category(4L, "WORD", "Verbal Fluency", "Anagram resolution and word ladders", "book");
        Category patternCategory = new Category(5L, "PATTERN", "Pattern Recognition", "Matrix reasoning and visual patterns", "shapes");

        categoryMap.put(numberCategory.getId(), numberCategory);
        categoryMap.put(logicCategory.getId(), logicCategory);
        categoryMap.put(memoryCategory.getId(), memoryCategory);
        categoryMap.put(wordCategory.getId(), wordCategory);
        categoryMap.put(patternCategory.getId(), patternCategory);

        // 2. Sample Puzzles across categories
        // Number Puzzle (Tier 1)
        Puzzle p1 = new Puzzle(1L, 1L, PuzzleType.NUMBER, DifficultyTier.TIER_1,
                "Simple Arithmetic", "Solve: 15 + 27 = ?", 40, 100,
                "{\"equation\": \"15 + 27 = ?\"}", "{\"expectedValue\": 42}");

        // Logic Puzzle (Tier 2)
        Puzzle p2 = new Puzzle(2L, 2L, PuzzleType.LOGIC, DifficultyTier.TIER_2,
                "Truth Deduction", "If all Bloops are Razzies, and all Razzies are Liddles, are all Bloops definitely Liddles? (A: Yes, B: No)", 30, 150,
                "{\"question\": \"Are all Bloops Liddles?\", \"options\": [\"A: Yes\", \"B: No\"]}", "{\"correctOption\": \"A\"}");

        // Memory Puzzle (Tier 2)
        Puzzle p3 = new Puzzle(3L, 3L, PuzzleType.MEMORY, DifficultyTier.TIER_2,
                "Sequence Recall", "Recall the following sequence: 4, 8, 1, 9", 30, 150,
                "{\"length\": 4}", "{\"ordered\": true, \"sequence\": [\"4\", \"8\", \"1\", \"9\"]}");

        // Word Puzzle (Tier 1)
        Puzzle p4 = new Puzzle(4L, 4L, PuzzleType.WORD, DifficultyTier.TIER_1,
                "Anagram Challenge", "Unscramble the letters: E P P L A", 40, 100,
                "{\"scrambled\": \"EPPLA\"}", "{\"targetWord\": \"APPLE\"}");

        // Pattern Puzzle (Tier 3)
        Puzzle p5 = new Puzzle(5L, 5L, PuzzleType.PATTERN, DifficultyTier.TIER_3,
                "Matrix Completion", "Identify the pattern that completes the matrix: [1, 2], [3, ?]", 25, 220,
                "{\"matrix\": [[1, 2], [3, null]]}", "{\"correctPatternId\": \"4\"}");

        puzzleMap.put(p1.getId(), p1);
        puzzleMap.put(p2.getId(), p2);
        puzzleMap.put(p3.getId(), p3);
        puzzleMap.put(p4.getId(), p4);
        puzzleMap.put(p5.getId(), p5);
    }

    public List<Category> getAllCategories() {
        return Collections.unmodifiableList(new ArrayList<>(categoryMap.values()));
    }

    public Optional<Category> getCategoryById(Long id) {
        return Optional.ofNullable(categoryMap.get(id));
    }

    public Optional<Puzzle> getPuzzleById(Long id) {
        return Optional.ofNullable(puzzleMap.get(id));
    }

    public List<Puzzle> getPuzzlesByCategory(Long categoryId) {
        List<Puzzle> results = new ArrayList<>();
        for (Puzzle puzzle : puzzleMap.values()) {
            if (puzzle.getCategoryId().equals(categoryId)) {
                results.add(puzzle);
            }
        }
        return Collections.unmodifiableList(results);
    }
}
