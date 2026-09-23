package com.mindmaze.attempt.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Pure Java domain model representing an immutable attempt record for a puzzle challenge.
 * Note: No JPA annotations in Phase 1.
 */
public class PuzzleAttempt {
    private Long id;
    private Long userId;
    private Long puzzleId;
    private boolean correct;
    private long timeSpentMs;
    private int scoreEarned;
    private int xpEarned;
    private String userSubmissionJson;
    private Instant attemptedAt;

    public PuzzleAttempt() {
        this.attemptedAt = Instant.now();
    }

    public PuzzleAttempt(Long id, Long userId, Long puzzleId, boolean correct,
                         long timeSpentMs, int scoreEarned, int xpEarned,
                         String userSubmissionJson) {
        this.id = id;
        this.userId = userId;
        this.puzzleId = puzzleId;
        this.correct = correct;
        this.timeSpentMs = timeSpentMs;
        this.scoreEarned = scoreEarned;
        this.xpEarned = xpEarned;
        this.userSubmissionJson = userSubmissionJson;
        this.attemptedAt = Instant.now();
    }

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

    public Long getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(Long puzzleId) {
        this.puzzleId = puzzleId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public long getTimeSpentMs() {
        return timeSpentMs;
    }

    public void setTimeSpentMs(long timeSpentMs) {
        this.timeSpentMs = timeSpentMs;
    }

    public int getScoreEarned() {
        return scoreEarned;
    }

    public void setScoreEarned(int scoreEarned) {
        this.scoreEarned = scoreEarned;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }

    public String getUserSubmissionJson() {
        return userSubmissionJson;
    }

    public void setUserSubmissionJson(String userSubmissionJson) {
        this.userSubmissionJson = userSubmissionJson;
    }

    public Instant getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(Instant attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleAttempt that = (PuzzleAttempt) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PuzzleAttempt{" +
                "id=" + id +
                ", userId=" + userId +
                ", puzzleId=" + puzzleId +
                ", correct=" + correct +
                ", timeSpentMs=" + timeSpentMs +
                ", scoreEarned=" + scoreEarned +
                ", xpEarned=" + xpEarned +
                ", attemptedAt=" + attemptedAt +
                '}';
    }
}
