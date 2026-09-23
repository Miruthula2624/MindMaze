package com.mindmaze;

import com.mindmaze.attempt.domain.PuzzleAttempt;
import com.mindmaze.difficulty.AdaptiveDifficultyEngine;
import com.mindmaze.difficulty.TierAdjustmentDecision;
import com.mindmaze.difficulty.UserCategoryPerformance;
import com.mindmaze.puzzle.domain.Category;
import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleClientView;
import com.mindmaze.puzzle.domain.PuzzleType;
import com.mindmaze.puzzle.strategy.EvaluationResult;
import com.mindmaze.puzzle.strategy.PuzzleStrategy;
import com.mindmaze.puzzle.strategy.PuzzleStrategyFactory;
import com.mindmaze.scoring.ScoreBreakdown;
import com.mindmaze.scoring.ScoreCalculator;
import com.mindmaze.scoring.XpBreakdown;
import com.mindmaze.scoring.XpCalculator;
import com.mindmaze.user.domain.User;
import com.mindmaze.user.domain.UserProfile;
import com.mindmaze.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase 1.13 End-to-End Simulation Test.
 * Pure Java simulation verifying the complete solve-to-progression lifecycle across all core domain components
 * without web frameworks, databases, or Spring configurations.
 */
class Phase1EndToEndSimulationTest {

    private PuzzleStrategyFactory strategyFactory;
    private ScoreCalculator scoreCalculator;
    private XpCalculator xpCalculator;
    private AdaptiveDifficultyEngine difficultyEngine;

    @BeforeEach
    void setUp() {
        strategyFactory = PuzzleStrategyFactory.createWithDefaultStrategies();
        scoreCalculator = new ScoreCalculator();
        xpCalculator = new XpCalculator();
        difficultyEngine = new AdaptiveDifficultyEngine();
    }

    @Test
    @DisplayName("Complete Cognitive Challenge Simulation: Solving, Scoring, XP, Progression, and Adaptive Adjustment")
    void shouldSimulateCompleteSolvingLifecycle() {
        // =========================================================================
        // Step 1: User & Profile Initialization
        // =========================================================================
        User user = new User(100L, "alex_mind", "alex@example.com", "hashed_pw", UserRole.ROLE_USER);
        UserProfile userProfile = new UserProfile(1L, user.getId(), "Alex Mind");

        assertThat(userProfile.getCurrentLevel()).isEqualTo(1);
        assertThat(userProfile.getTotalXp()).isEqualTo(0);
        assertThat(userProfile.getCurrentStreak()).isEqualTo(0);

        // =========================================================================
        // Step 2: Category & Adaptive Performance State Setup (Cold-Start at Tier 1)
        // =========================================================================
        Category mathCategory = new Category(10L, "NUMBER", "Number Reasoning", "Math puzzles", "calculator");
        UserCategoryPerformance userPerf = new UserCategoryPerformance(1L, user.getId(), mathCategory.getId());

        assertThat(userPerf.getCurrentTier()).isEqualTo(DifficultyTier.TIER_1);
        assertThat(userPerf.getTotalAttempts()).isEqualTo(0);

        // =========================================================================
        // Step 3: Puzzle Retrieval & Client View Protection (Strip Solution)
        // =========================================================================
        Puzzle arithmeticPuzzle = new Puzzle(
                501L,
                mathCategory.getId(),
                PuzzleType.NUMBER,
                DifficultyTier.TIER_1,
                "Equation Balance",
                "Solve: 12 + ? = 25",
                40, // targetTimeSeconds
                100, // basePoints
                "{\"equation\": \"12 + ? = 25\"}",
                "{\"expectedValue\": 13}" // Strictly server-side
        );

        // Client gets sanitized view without solutionDataJson
        PuzzleClientView clientView = arithmeticPuzzle.toClientView();
        assertThat(clientView.id()).isEqualTo(501L);
        assertThat(clientView.title()).isEqualTo("Equation Balance");
        assertThat(clientView.basePoints()).isEqualTo(100);
        // Verify solution is not present in client view
        assertThat(clientView.toString()).doesNotContain("expectedValue");

        // =========================================================================
        // Step 4: Submission & Evaluation through Strategy
        // =========================================================================
        String userSubmissionJson = "{\"submittedValue\": 13}";
        long timeSpentMs = 12000; // 12 seconds (<= 20s, which is 50% of 40s target time -> fast solve!)

        PuzzleStrategy strategy = strategyFactory.getStrategy(arithmeticPuzzle.getPuzzleType());
        EvaluationResult evalResult = strategy.evaluate(arithmeticPuzzle, userSubmissionJson, timeSpentMs);

        assertThat(evalResult.isCorrect()).isTrue();
        assertThat(evalResult.timeSpentMs()).isEqualTo(12000);
        assertThat(evalResult.targetTimeSeconds()).isEqualTo(40);
        assertThat(evalResult.difficultyTier()).isEqualTo(DifficultyTier.TIER_1);

        // =========================================================================
        // Step 5: Score Calculation
        // =========================================================================
        int sessionStreak = 1; // 1st solve in this session
        ScoreBreakdown scoreBreakdown = scoreCalculator.calculateScore(evalResult, sessionStreak);

        assertThat(scoreBreakdown.basePoints()).isEqualTo(100);
        assertThat(scoreBreakdown.timeBonusMultiplier()).isEqualTo(0.50); // Full 50% speed bonus
        assertThat(scoreBreakdown.streakMultiplier()).isEqualTo(1.0); // 1st solve
        // Final Score = 100 * (1 + 0.50) * 1.0 = 150 points
        assertThat(scoreBreakdown.finalScore()).isEqualTo(150);

        // =========================================================================
        // Step 6: XP & Progression Calculation
        // =========================================================================
        boolean isDailyChallenge = false;
        XpBreakdown xpBreakdown = xpCalculator.calculateXp(evalResult, isDailyChallenge);

        assertThat(xpBreakdown.baseXp()).isEqualTo(20); // Tier 1 base XP = 20
        assertThat(xpBreakdown.totalXpAwarded()).isEqualTo(20);

        // =========================================================================
        // Step 7: Adaptive Difficulty Adjustment
        // =========================================================================
        TierAdjustmentDecision tierDecision1 = difficultyEngine.evaluateAndAdjustTier(userPerf, evalResult);

        // 1st attempt during cold-start: assessment ongoing, maintained at Tier 1
        assertThat(tierDecision1.action()).isEqualTo(TierAdjustmentDecision.Action.MAINTAINED);
        assertThat(userPerf.getCurrentTier()).isEqualTo(DifficultyTier.TIER_1);
        assertThat(userPerf.getTotalAttempts()).isEqualTo(1);
        assertThat(userPerf.getConsecutiveCorrect()).isEqualTo(1);

        // =========================================================================
        // Step 8: Persistence Log Creation & User Profile State Update
        // =========================================================================
        PuzzleAttempt attemptLog = new PuzzleAttempt(
                1001L,
                user.getId(),
                arithmeticPuzzle.getId(),
                evalResult.isCorrect(),
                evalResult.timeSpentMs(),
                scoreBreakdown.finalScore(),
                xpBreakdown.totalXpAwarded(),
                evalResult.sanitizedSubmission()
        );

        assertThat(attemptLog.getScoreEarned()).isEqualTo(150);
        assertThat(attemptLog.getXpEarned()).isEqualTo(20);

        // Update User Profile
        userProfile.addXp(attemptLog.getXpEarned());
        userProfile.incrementPuzzlesSolved();
        userProfile.recordDailyActivity(LocalDate.of(2026, 9, 23));

        assertThat(userProfile.getTotalXp()).isEqualTo(20);
        assertThat(userProfile.getTotalPuzzlesSolved()).isEqualTo(1);
        assertThat(userProfile.getCurrentStreak()).isEqualTo(1);

        // =========================================================================
        // Step 9: Follow-up Attempt Testing Cold-Start Rapid Calibration (2nd fast solve)
        // =========================================================================
        Puzzle puzzle2 = new Puzzle(
                502L, mathCategory.getId(), PuzzleType.NUMBER, DifficultyTier.TIER_1,
                "Sum", "5 + 5", 40, 100, "{}", "{\"expectedValue\": 10}"
        );
        EvaluationResult eval2 = strategy.evaluate(puzzle2, "{\"submittedValue\": 10}", 10000); // 10s fast solve
        TierAdjustmentDecision tierDecision2 = difficultyEngine.evaluateAndAdjustTier(userPerf, eval2);

        // Rapid cold-start calibration: 2 consecutive fast wins in Tier 1 -> Promoted to Tier 2!
        assertThat(tierDecision2.action()).isEqualTo(TierAdjustmentDecision.Action.PROMOTED);
        assertThat(tierDecision2.newTier()).isEqualTo(DifficultyTier.TIER_2);
        assertThat(userPerf.getCurrentTier()).isEqualTo(DifficultyTier.TIER_2);
    }
}
