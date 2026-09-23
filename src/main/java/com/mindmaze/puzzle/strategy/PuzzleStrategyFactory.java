package com.mindmaze.puzzle.strategy;

import com.mindmaze.common.exception.StrategyNotFoundException;
import com.mindmaze.puzzle.domain.PuzzleType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Registry and factory for retrieving category-specific PuzzleStrategy instances.
 * Enables dynamic polymorphic dispatch without client knowledge of concrete implementations.
 */
public class PuzzleStrategyFactory {

    private final Map<PuzzleType, PuzzleStrategy> strategyRegistry;

    public PuzzleStrategyFactory() {
        this.strategyRegistry = new EnumMap<>(PuzzleType.class);
    }

    public PuzzleStrategyFactory(Collection<PuzzleStrategy> strategies) {
        this();
        if (strategies != null) {
            for (PuzzleStrategy strategy : strategies) {
                registerStrategy(strategy);
            }
        }
    }

    /**
     * Registers a strategy into the registry.
     */
    public void registerStrategy(PuzzleStrategy strategy) {
        if (strategy != null && strategy.getSupportedType() != null) {
            strategyRegistry.put(strategy.getSupportedType(), strategy);
        }
    }

    /**
     * Retrieves the registered strategy for a given PuzzleType.
     *
     * @param type the cognitive puzzle category
     * @return the associated PuzzleStrategy
     * @throws StrategyNotFoundException if no strategy is registered for the type
     */
    public PuzzleStrategy getStrategy(PuzzleType type) {
        if (type == null || !strategyRegistry.containsKey(type)) {
            throw new StrategyNotFoundException(type);
        }
        return strategyRegistry.get(type);
    }

    /**
     * Returns the count of registered strategies.
     */
    public int getRegisteredCount() {
        return strategyRegistry.size();
    }

    /**
     * Static factory method that initializes a factory loaded with all 5 default cognitive strategies.
     */
    public static PuzzleStrategyFactory createWithDefaultStrategies() {
        PuzzleStrategyFactory factory = new PuzzleStrategyFactory();
        factory.registerStrategy(new NumberPuzzleStrategy());
        factory.registerStrategy(new LogicPuzzleStrategy());
        factory.registerStrategy(new MemoryPuzzleStrategy());
        factory.registerStrategy(new WordPuzzleStrategy());
        factory.registerStrategy(new PatternPuzzleStrategy());
        return factory;
    }
}
