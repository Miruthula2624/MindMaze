package com.mindmaze.user.domain;

/**
 * User roles defining access permissions within MindMaze.
 */
public enum UserRole {
    ROLE_USER("Standard User", "Can solve puzzles, view personal analytics, and compete in daily challenges"),
    ROLE_ADMIN("Administrator", "Can manage puzzle catalog, review system analytics, and curate content");

    private final String roleName;
    private final String description;

    UserRole(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }
}
