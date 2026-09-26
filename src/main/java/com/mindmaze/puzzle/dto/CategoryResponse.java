package com.mindmaze.puzzle.dto;

import com.mindmaze.puzzle.domain.Category;

/**
 * DTO for delivering category details to clients.
 */
public record CategoryResponse(
        Long id,
        String code,
        String displayName,
        String description,
        String iconName
) {
    public static CategoryResponse fromDomain(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getDisplayName(),
                category.getDescription(),
                category.getIconName()
        );
    }
}
