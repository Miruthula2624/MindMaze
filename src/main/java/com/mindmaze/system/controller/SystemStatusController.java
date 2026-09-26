package com.mindmaze.system.controller;

import com.mindmaze.config.MindMazeConstants;
import com.mindmaze.system.dto.SystemStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * REST controller exposing basic system status and health information.
 */
@RestController
@RequestMapping("/api/v1/system")
public class SystemStatusController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping("/status")
    public ResponseEntity<SystemStatusResponse> getStatus() {
        SystemStatusResponse response = new SystemStatusResponse(
                "UP",
                MindMazeConstants.APP_NAME,
                MindMazeConstants.APP_VERSION,
                activeProfile,
                Instant.now()
        );
        return ResponseEntity.ok(response);
    }
}
