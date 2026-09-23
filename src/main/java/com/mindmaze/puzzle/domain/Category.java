package com.mindmaze.puzzle.domain;

import java.util.Objects;

/**
 * Pure Java domain model representing a cognitive category.
 * Note: No JPA annotations in Phase 1.
 */
public class Category {
    private Long id;
    private String code;
    private String displayName;
    private String description;
    private String iconName;
    private boolean active;

    public Category() {
        this.active = true;
    }

    public Category(Long id, String code, String displayName, String description, String iconName) {
        this.id = id;
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.iconName = iconName;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) || Objects.equals(code, category.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", displayName='" + displayName + '\'' +
                ", active=" + active +
                '}';
    }
}
