package com.mindmaze.puzzle.strategy;

import com.mindmaze.common.exception.StrategyNotFoundException;
import com.mindmaze.puzzle.domain.PuzzleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PuzzleStrategyFactoryTest {

    @Test
    @DisplayName("Should successfully retrieve all 5 default puzzle strategies")
    void shouldRetrieveAllDefaultStrategies() {
        PuzzleStrategyFactory factory = PuzzleStrategyFactory.createWithDefaultStrategies();

        assertThat(factory.getRegisteredCount()).isEqualTo(5);
        assertThat(factory.getStrategy(PuzzleType.NUMBER)).isInstanceOf(NumberPuzzleStrategy.class);
        assertThat(factory.getStrategy(PuzzleType.LOGIC)).isInstanceOf(LogicPuzzleStrategy.class);
        assertThat(factory.getStrategy(PuzzleType.MEMORY)).isInstanceOf(MemoryPuzzleStrategy.class);
        assertThat(factory.getStrategy(PuzzleType.WORD)).isInstanceOf(WordPuzzleStrategy.class);
        assertThat(factory.getStrategy(PuzzleType.PATTERN)).isInstanceOf(PatternPuzzleStrategy.class);
    }

    @Test
    @DisplayName("Should throw StrategyNotFoundException when requested type is unregistered")
    void shouldThrowWhenStrategyUnregistered() {
        PuzzleStrategyFactory emptyFactory = new PuzzleStrategyFactory();

        assertThatThrownBy(() -> emptyFactory.getStrategy(PuzzleType.NUMBER))
                .isInstanceOf(StrategyNotFoundException.class)
                .hasMessageContaining("NUMBER");
    }

    @Test
    @DisplayName("Should throw StrategyNotFoundException when requested type is null")
    void shouldThrowWhenTypeIsNull() {
        PuzzleStrategyFactory factory = PuzzleStrategyFactory.createWithDefaultStrategies();

        assertThatThrownBy(() -> factory.getStrategy(null))
                .isInstanceOf(StrategyNotFoundException.class);
    }
}
