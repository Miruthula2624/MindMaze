package com.mindmaze.user.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Pure Java domain model representing user progression, stats, and streaks.
 * Encapsulates core domain business methods for XP additions and streak state transitions.
 */
public class UserProfile {
    private Long id;
    private Long userId;
    private String displayName;
    private long totalXp;
    private int currentLevel;
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastActiveDate;
    private int totalPuzzlesSolved;

    public UserProfile() {
        this.totalXp = 0;
        this.currentLevel = 1;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.totalPuzzlesSolved = 0;
    }

    public UserProfile(Long id, Long userId, String displayName) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.totalXp = 0;
        this.currentLevel = 1;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.totalPuzzlesSolved = 0;
    }

    /**
     * Adds XP to user profile.
     */
    public void addXp(long xpEarned) {
        if (xpEarned > 0) {
            this.totalXp += xpEarned;
        }
    }

    /**
     * Updates current level if progression indicates a level change.
     */
    public boolean updateLevel(int newLevel) {
        if (newLevel > this.currentLevel) {
            this.currentLevel = newLevel;
            return true; // Leveled up!
        }
        return false;
    }

    /**
     * Increments solved count when a puzzle is successfully completed.
     */
    public void incrementPuzzlesSolved() {
        this.totalPuzzlesSolved++;
    }

    /**
     * Updates user's daily activity and recalculates streak according to calendar day boundaries:
     * - Same day: streak maintained (already credited).
     * - Consecutive day (diff == 1): currentStreak increments by 1.
     * - Broken streak (diff > 1 or null): resets currentStreak to 1.
     */
    public void recordDailyActivity(LocalDate activityDate) {
        if (activityDate == null) {
            return;
        }

        if (this.lastActiveDate == null) {
            this.currentStreak = 1;
        } else {
            long daysDifference = ChronoUnit.DAYS.between(this.lastActiveDate, activityDate);
            if (daysDifference == 1) {
                this.currentStreak++;
            } else if (daysDifference > 1) {
                this.currentStreak = 1; // Streak broken, restarted
            }
            // If daysDifference == 0, same day activity, streak remains unchanged.
        }

        if (this.currentStreak > this.longestStreak) {
            this.longestStreak = this.currentStreak;
        }

        this.lastActiveDate = activityDate;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(long totalXp) {
        this.totalXp = totalXp;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public int getTotalPuzzlesSolved() {
        return totalPuzzlesSolved;
    }

    public void setTotalPuzzlesSolved(int totalPuzzlesSolved) {
        this.totalPuzzlesSolved = totalPuzzlesSolved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId=" + userId +
                ", displayName='" + displayName + '\'' +
                ", totalXp=" + totalXp +
                ", currentLevel=" + currentLevel +
                ", currentStreak=" + currentStreak +
                ", longestStreak=" + longestStreak +
                ", lastActiveDate=" + lastActiveDate +
                '}';
    }
}
