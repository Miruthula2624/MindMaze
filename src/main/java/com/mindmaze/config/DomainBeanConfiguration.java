package com.mindmaze.config;

import com.mindmaze.difficulty.AdaptiveDifficultyEngine;
import com.mindmaze.puzzle.strategy.PuzzleStrategyFactory;
import com.mindmaze.scoring.ScoreCalculator;
import com.mindmaze.scoring.XpCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration bridge registering pure Java domain engines into the Spring ApplicationContext.
 * Ensures the Phase 1 domain layer remains 100% framework-independent and free of Spring annotations.
 */
@Configuration
public class DomainBeanConfiguration {

    @Bean
    public PuzzleStrategyFactory puzzleStrategyFactory() {
        return PuzzleStrategyFactory.createWithDefaultStrategies();
    }

    @Bean
    public ScoreCalculator scoreCalculator() {
        return new ScoreCalculator();
    }

    @Bean
    public XpCalculator xpCalculator() {
        return new XpCalculator();
    }

    @Bean
    public AdaptiveDifficultyEngine adaptiveDifficultyEngine() {
        return new AdaptiveDifficultyEngine();
    }
}
