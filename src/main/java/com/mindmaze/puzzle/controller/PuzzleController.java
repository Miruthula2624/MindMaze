package com.mindmaze.puzzle.controller;

import com.mindmaze.puzzle.domain.PuzzleClientView;
import com.mindmaze.puzzle.dto.CategoryResponse;
import com.mindmaze.puzzle.service.PuzzleApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for browsing puzzle categories and retrieving client-safe challenge payloads.
 * Strictly avoids exposing solutionDataJson to clients.
 */
@RestController
@RequestMapping("/api/v1/puzzles")
public class PuzzleController {

    private final PuzzleApplicationService puzzleApplicationService;

    public PuzzleController(PuzzleApplicationService puzzleApplicationService) {
        this.puzzleApplicationService = puzzleApplicationService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(puzzleApplicationService.getCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuzzleClientView> getPuzzleById(@PathVariable Long id) {
        return ResponseEntity.ok(puzzleApplicationService.getPuzzleClientView(id));
    }

    @GetMapping
    public ResponseEntity<List<PuzzleClientView>> getPuzzlesByCategory(@RequestParam Long categoryId) {
        return ResponseEntity.ok(puzzleApplicationService.getPuzzlesByCategory(categoryId));
    }
}
