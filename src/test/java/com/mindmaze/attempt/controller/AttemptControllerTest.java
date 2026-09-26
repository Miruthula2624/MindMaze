package com.mindmaze.attempt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmaze.attempt.dto.AttemptResultResponse;
import com.mindmaze.attempt.dto.SubmitAttemptRequest;
import com.mindmaze.attempt.service.AttemptApplicationService;
import com.mindmaze.common.error.GlobalExceptionHandler;
import com.mindmaze.puzzle.domain.DifficultyTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttemptController.class)
@Import(GlobalExceptionHandler.class)
class AttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttemptApplicationService attemptApplicationService;

    @Test
    @DisplayName("POST /api/v1/attempts with valid payload returns 200 OK with AttemptResultResponse")
    void shouldSubmitValidAttempt() throws Exception {
        SubmitAttemptRequest request = new SubmitAttemptRequest(
                1L,
                "{\"submittedValue\": 42}",
                12000,
                1
        );

        AttemptResultResponse response = new AttemptResultResponse(
                true,
                150,
                20,
                12000,
                DifficultyTier.TIER_1,
                DifficultyTier.TIER_1,
                "MAINTAINED",
                "Cold-start assessment ongoing.",
                "Exceptional solve! Lightning fast reaction time."
        );

        when(attemptApplicationService.processAttempt(any(SubmitAttemptRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCorrect").value(true))
                .andExpect(jsonPath("$.scoreEarned").value(150))
                .andExpect(jsonPath("$.xpEarned").value(20))
                .andExpect(jsonPath("$.tierAction").value("MAINTAINED"));
    }

    @Test
    @DisplayName("POST /api/v1/attempts with timeSpentMs < 500 triggers 400 Bad Request with RFC 7807 ProblemDetail")
    void shouldRejectSub500msAttempt() throws Exception {
        SubmitAttemptRequest invalidRequest = new SubmitAttemptRequest(
                1L,
                "{\"submittedValue\": 42}",
                300, // Invalid: < 500ms
                1
        );

        mockMvc.perform(post("/api/v1/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.invalidFields.timeSpentMs").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/attempts with null puzzleId triggers 400 Bad Request with validation error details")
    void shouldRejectNullPuzzleId() throws Exception {
        SubmitAttemptRequest invalidRequest = new SubmitAttemptRequest(
                null,
                "{\"submittedValue\": 42}",
                5000,
                1
        );

        mockMvc.perform(post("/api/v1/attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.invalidFields.puzzleId").isNotEmpty());
    }
}
