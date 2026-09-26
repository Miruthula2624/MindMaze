package com.mindmaze.system.dto;

import java.time.Instant;

/**
 * DTO representing the application system health and metadata status.
 */
public record SystemStatusResponse(
        String status,
        String application,
        String version,
        String environment,
        Instant timestamp
) {
}
