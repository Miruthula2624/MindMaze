package com.mindmaze.difficulty;

import com.mindmaze.puzzle.domain.DifficultyTier;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Pure Java domain state tracking a user's performance and adaptive standing within a specific cognitive category.
 * Note: No JPA annotations in Phase 1.
 */
public class UserCategoryPerformance {
    private Long id;
    private Long userId;
    private Long categoryId;
    private DifficultyTier currentTier;
    private int consecutiveCorrect;
    private int consecutiveFailures;
    private int totalAttempts;
    private int totalCorrect;
    private long averageTimeMs;
    private final Deque<Boolean> recentOutcomesWindow; // Rolling window of last 5 attempts

    public UserCategoryPerformance() {
        this.currentTier = DifficultyTier.TIER_1;
        this.consecutiveCorrect = 0;
        this.consecutiveFailures = 0;
        this.totalAttempts = 0;
        this.totalCorrect = 0;
        this.averageTimeMs = 0;
        this.recentOutcomesWindow = new ArrayDeque<>(5);
    }

    public UserCategoryPerformance(Long id, Long userId, Long categoryId) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.currentTier = DifficultyTier.TIER_1;
        this.consecutiveCorrect = 0;
        this.consecutiveFailures = 0;
        this.totalAttempts = 0;
        this.totalCorrect = 0;
        this.averageTimeMs = 0;
        this.recentOutcomesWindow = new ArrayDeque<>(5);
    }

    /**
     * Records a new attempt result and updates counters and rolling history window.
     */
    public void recordAttempt(boolean isCorrect, long timeSpentMs) {
        this.totalAttempts++;
        if (isCorrect) {
            this.totalCorrect++;
            this.consecutiveCorrect++;
            this.consecutiveFailures = 0;
        } else {
            this.consecutiveFailures++;
            this.consecutiveCorrect = 0;
        }

        // Update running average time
        if (this.totalAttempts == 1) {
            this.averageTimeMs = timeSpentMs;
        } else {
            this.averageTimeMs = ((this.averageTimeMs * (this.totalAttempts - 1)) + timeSpentMs) / this.totalAttempts;
        }

        // Maintain rolling window of last 5 outcomes
        if (this.recentOutcomesWindow.size() >= 5) {
            this.recentOutcomesWindow.pollFirst();
        }
        this.recentOutcomesWindow.addLast(isCorrect);
    }

    /**
     * Computes the rolling accuracy over the recent attempts window (0.0 to 1.0).
     */
    public double getRollingAccuracy() {
        if (recentOutcomesWindow.isEmpty()) {
            return 0.0;
        }
        int correctCount = 0;
        for (boolean outcome : recentOutcomesWindow) {
            if (outcome) {
                correctCount++;
            }
        }
        return (double) correctCount / recentOutcomesWindow.size();
    }

    public void promoteTier() {
        this.currentTier = this.currentTier.nextTier();
        this.consecutiveCorrect = 0;
        this.consecutiveFailures = 0;
    }

    public void demoteTier() {
        this.currentTier = this.currentTier.previousTier();
        this.consecutiveCorrect = 0;
        this.consecutiveFailures = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public DifficultyTier getCurrentTier() {
        return currentTier;
    }

    public void setCurrentTier(DifficultyTier currentTier) {
        this.currentTier = currentTier != null ? currentTier : DifficultyTier.TIER_1;
    }

    public int getConsecutiveCorrect() {
        return consecutiveCorrect;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public long getAverageTimeMs() {
        return averageTimeMs;
    }

    public List<Boolean> getRecentOutcomes() {
        return new ArrayList<>(recentOutcomesWindow);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCategoryPerformance that = (UserCategoryPerformance) o;
        return Objects.equals(userId, that.userId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, categoryId);
    }

    @Override
    public String toString() {
        return "UserCategoryPerformance{" +
                "userId=" + userId +
                ", categoryId=" + categoryId +
                ", currentTier=" + currentTier +
                ", totalAttempts=" + totalAttempts +
                ", consecutiveCorrect=" + consecutiveCorrect +
                ", consecutiveFailures=" + consecutiveFailures +
                '}';
    }
}
