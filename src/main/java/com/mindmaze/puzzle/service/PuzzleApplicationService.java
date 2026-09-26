package com.mindmaze.puzzle.service;

import com.mindmaze.common.exception.ResourceNotFoundException;
import com.mindmaze.puzzle.catalog.InMemoryPuzzleCatalog;
import com.mindmaze.puzzle.domain.Category;
import com.mindmaze.puzzle.domain.Puzzle;
import com.mindmaze.puzzle.domain.PuzzleClientView;
import com.mindmaze.puzzle.dto.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service orchestrating puzzle catalog queries and mapping domain entities
 * into client-safe views without exposing solution payloads.
 */
@Service
public class PuzzleApplicationService {

    private final InMemoryPuzzleCatalog catalog;

    public PuzzleApplicationService(InMemoryPuzzleCatalog catalog) {
        this.catalog = catalog;
    }

    public List<CategoryResponse> getCategories() {
        return catalog.getAllCategories().stream()
                .map(CategoryResponse::fromDomain)
                .toList();
    }

    public PuzzleClientView getPuzzleClientView(Long puzzleId) {
        return catalog.getPuzzleById(puzzleId)
                .map(Puzzle::toClientView)
                .orElseThrow(() -> new ResourceNotFoundException("Puzzle with ID " + puzzleId + " not found."));
    }

    public List<PuzzleClientView> getPuzzlesByCategory(Long categoryId) {
        Category category = catalog.getCategoryById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found."));

        return catalog.getPuzzlesByCategory(category.getId()).stream()
                .map(Puzzle::toClientView)
                .toList();
    }

    /**
     * Package-private helper to retrieve raw internal puzzle domain entity (including solution)
     * for verification by application attempt services.
     */
    public Puzzle getInternalPuzzle(Long puzzleId) {
        return catalog.getPuzzleById(puzzleId)
                .orElseThrow(() -> new ResourceNotFoundException("Puzzle with ID " + puzzleId + " not found."));
    }
}
