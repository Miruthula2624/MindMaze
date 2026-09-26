package com.mindmaze.puzzle.controller;

import com.mindmaze.common.error.GlobalExceptionHandler;
import com.mindmaze.common.exception.ResourceNotFoundException;
import com.mindmaze.puzzle.domain.DifficultyTier;
import com.mindmaze.puzzle.domain.PuzzleClientView;
import com.mindmaze.puzzle.domain.PuzzleType;
import com.mindmaze.puzzle.dto.CategoryResponse;
import com.mindmaze.puzzle.service.PuzzleApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PuzzleController.class)
@Import(GlobalExceptionHandler.class)
class PuzzleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PuzzleApplicationService puzzleApplicationService;

    @Test
    @DisplayName("GET /api/v1/puzzles/categories returns 200 OK with list of categories")
    void shouldReturnCategories() throws Exception {
        CategoryResponse cat1 = new CategoryResponse(1L, "NUMBER", "Number Reasoning", "Math puzzles", "calc");
        when(puzzleApplicationService.getCategories()).thenReturn(List.of(cat1));

        mockMvc.perform(get("/api/v1/puzzles/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("NUMBER"))
                .andExpect(jsonPath("$[0].displayName").value("Number Reasoning"));
    }

    @Test
    @DisplayName("GET /api/v1/puzzles/{id} returns 200 OK with PuzzleClientView and never reveals solution")
    void shouldReturnPuzzleClientViewWithoutSolution() throws Exception {
        PuzzleClientView clientView = new PuzzleClientView(
                1L, 1L, PuzzleType.NUMBER, DifficultyTier.TIER_1,
                "Math Puzzle", "Solve equation", 40, 100, "{\"equation\": \"2+2\"}"
        );
        when(puzzleApplicationService.getPuzzleClientView(1L)).thenReturn(clientView);

        mockMvc.perform(get("/api/v1/puzzles/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Math Puzzle"))
                .andExpect(jsonPath("$.solutionDataJson").doesNotExist())
                .andExpect(jsonPath("$.solution").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/puzzles/{id} with non-existent ID returns 404 ProblemDetail")
    void shouldReturn404WhenPuzzleNotFound() throws Exception {
        when(puzzleApplicationService.getPuzzleClientView(999L))
                .thenThrow(new ResourceNotFoundException("Puzzle with ID 999 not found."));

        mockMvc.perform(get("/api/v1/puzzles/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value("Puzzle with ID 999 not found."));
    }
}
