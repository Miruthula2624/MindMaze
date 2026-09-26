package com.mindmaze.attempt.controller;

import com.mindmaze.attempt.dto.AttemptResultResponse;
import com.mindmaze.attempt.dto.SubmitAttemptRequest;
import com.mindmaze.attempt.service.AttemptApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for puzzle attempt submissions.
 * Validates incoming request payloads and delegates orchestration to AttemptApplicationService.
 */
@RestController
@RequestMapping("/api/v1/attempts")
public class AttemptController {

    private final AttemptApplicationService attemptApplicationService;

    public AttemptController(AttemptApplicationService attemptApplicationService) {
        this.attemptApplicationService = attemptApplicationService;
    }

    @PostMapping
    public ResponseEntity<AttemptResultResponse> submitAttempt(@Valid @RequestBody SubmitAttemptRequest request) {
        AttemptResultResponse response = attemptApplicationService.processAttempt(request);
        return ResponseEntity.ok(response);
    }
}
