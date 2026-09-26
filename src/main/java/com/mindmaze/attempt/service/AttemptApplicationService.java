package com.mindmaze.attempt.service;

import com.mindmaze.attempt.dto.AttemptResultResponse;
import com.mindmaze.attempt.dto.SubmitAttemptRequest;
import com.mindmaze.difficulty.AdaptiveDifficultyEngine;
import com.mindmaze.difficulty.TierAdjustmentDecision;
import com.mindmaze.difficulty.UserCategoryPerformance;
import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.service.PuzzleApplicationService;
import com.mindmaze.puzzle.strategy.EvaluationResult;
import com.mindmaze.puzzle.strategy.PuzzleStrategy;
import com.mindmaze.puzzle.strategy.PuzzleStrategyFactory;
import com.mindmaze.scoring.ScoreBreakdown;
import com.mindmaze.scoring.ScoreCalculator;
import com.mindmaze.scoring.XpBreakdown;
import com.mindmaze.scoring.XpCalculator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application service orchestrating the complete puzzle submission and evaluation workflow.
 * <p>
 * Coordinates:
 * 1. Puzzle retrieval from the catalog
 * 2. Strategy evaluation
 * 3. Score calculation
 * 4. XP progression
 * 5. Adaptive difficulty tier transitions
 */
@Service
public class AttemptApplicationService {

    private final PuzzleApplicationService puzzleApplicationService;
    private final PuzzleStrategyFactory puzzleStrategyFactory;
    private final ScoreCalculator scoreCalculator;
    private final XpCalculator xpCalculator;
    private final AdaptiveDifficultyEngine adaptiveDifficultyEngine;

    // Temporary in-memory performance registry for Phase 2 (will be replaced by JPA in Phase 3)
    private final Map<Long, UserCategoryPerformance> inMemoryPerformanceMap = new ConcurrentHashMap<>();

    public AttemptApplicationService(
            PuzzleApplicationService puzzleApplicationService,
            PuzzleStrategyFactory puzzleStrategyFactory,
            ScoreCalculator scoreCalculator,
            XpCalculator xpCalculator,
            AdaptiveDifficultyEngine adaptiveDifficultyEngine
    ) {
        this.puzzleApplicationService = puzzleApplicationService;
        this.puzzleStrategyFactory = puzzleStrategyFactory;
        this.scoreCalculator = scoreCalculator;
        this.xpCalculator = xpCalculator;
        this.adaptiveDifficultyEngine = adaptiveDifficultyEngine;
    }

    public AttemptResultResponse processAttempt(SubmitAttemptRequest request) {
        // 1. Retrieve raw puzzle with server-side solution payload
        Puzzle puzzle = puzzleApplicationService.getInternalPuzzle(request.puzzleId());

        // 2. Resolve polymorphic strategy and evaluate correctness
        PuzzleStrategy strategy = puzzleStrategyFactory.getStrategy(puzzle.getPuzzleType());
        EvaluationResult evaluationResult = strategy.evaluate(
                puzzle,
                request.userSubmissionJson(),
                request.timeSpentMs()
        );

        // 3. Compute score via pure domain ScoreCalculator
        ScoreBreakdown scoreBreakdown = scoreCalculator.calculateScore(
                evaluationResult,
                request.sessionConsecutiveStreak()
        );

        // 4. Compute XP via pure domain XpCalculator
        XpBreakdown xpBreakdown = xpCalculator.calculateXp(evaluationResult, false);

        // 5. Update and evaluate adaptive difficulty
        UserCategoryPerformance performance = inMemoryPerformanceMap.computeIfAbsent(
                puzzle.getCategoryId(),
                catId -> new UserCategoryPerformance(1L, 1L, catId)
        );

        DifficultyTier previousTier = performance.getCurrentTier();
        TierAdjustmentDecision tierDecision = adaptiveDifficultyEngine.evaluateAndAdjustTier(
                performance,
                evaluationResult
        );

        // 6. Assemble and return typed response DTO
        return new AttemptResultResponse(
                evaluationResult.isCorrect(),
                scoreBreakdown.finalScore(),
                xpBreakdown.totalXpAwarded(),
                request.timeSpentMs(),
                previousTier,
                tierDecision.newTier(),
                tierDecision.action().name(),
                tierDecision.reason(),
                evaluationResult.feedbackMessage()
        );
    }
}
