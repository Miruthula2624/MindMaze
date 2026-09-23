package com.mindmaze.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileTest {

    private UserProfile profile;

    @BeforeEach
    void setUp() {
        profile = new UserProfile(1L, 100L, "BrainMaster");
    }

    @Test
    @DisplayName("Should initialize with default level 1, 0 XP, and 0 streak")
    void shouldInitializeWithDefaultValues() {
        assertThat(profile.getCurrentLevel()).isEqualTo(1);
        assertThat(profile.getTotalXp()).isEqualTo(0);
        assertThat(profile.getCurrentStreak()).isEqualTo(0);
        assertThat(profile.getLongestStreak()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should add XP correctly")
    void shouldAddXp() {
        profile.addXp(150);
        assertThat(profile.getTotalXp()).isEqualTo(150);

        profile.addXp(250);
        assertThat(profile.getTotalXp()).isEqualTo(400);
    }

    @Test
    @DisplayName("Streak: First day activity should initialize streak to 1")
    void shouldStartStreakOnFirstDay() {
        LocalDate today = LocalDate.of(2026, 9, 20);
        profile.recordDailyActivity(today);

        assertThat(profile.getCurrentStreak()).isEqualTo(1);
        assertThat(profile.getLongestStreak()).isEqualTo(1);
        assertThat(profile.getLastActiveDate()).isEqualTo(today);
    }

    @Test
    @DisplayName("Streak: Same-day activity should maintain current streak without double counting")
    void shouldMaintainStreakOnSameDayActivity() {
        LocalDate today = LocalDate.of(2026, 9, 20);
        profile.recordDailyActivity(today);
        profile.recordDailyActivity(today); // 2nd attempt on same day

        assertThat(profile.getCurrentStreak()).isEqualTo(1);
        assertThat(profile.getLongestStreak()).isEqualTo(1);
    }

    @Test
    @DisplayName("Streak: Consecutive day activity should increment current streak")
    void shouldIncrementStreakOnConsecutiveDay() {
        profile.recordDailyActivity(LocalDate.of(2026, 9, 20));
        profile.recordDailyActivity(LocalDate.of(2026, 9, 21));
        profile.recordDailyActivity(LocalDate.of(2026, 9, 22));

        assertThat(profile.getCurrentStreak()).isEqualTo(3);
        assertThat(profile.getLongestStreak()).isEqualTo(3);
    }

    @Test
    @DisplayName("Streak: Missing a day resets current streak to 1 while preserving longest streak")
    void shouldResetStreakWhenDayMissed() {
        profile.recordDailyActivity(LocalDate.of(2026, 9, 20));
        profile.recordDailyActivity(LocalDate.of(2026, 9, 21));
        profile.recordDailyActivity(LocalDate.of(2026, 9, 22)); // Streak = 3

        // Gap of 2 days
        profile.recordDailyActivity(LocalDate.of(2026, 9, 24));

        assertThat(profile.getCurrentStreak()).isEqualTo(1);
        assertThat(profile.getLongestStreak()).isEqualTo(3); // Preserved
    }
}
