package com.mindmaze.puzzle.domain;

import java.util.Objects;

/**
 * Pure Java domain model representing a puzzle in the catalog.
 * Holds both the client challenge payload and server-side solution payload.
 */
public class Puzzle {
    private Long id;
    private Long categoryId;
    private PuzzleType puzzleType;
    private DifficultyTier difficultyTier;
    private String title;
    private String instruction;
    private int targetTimeSeconds;
    private int basePoints;
    private String puzzleDataJson;
    private String solutionDataJson; // Kept strictly server-side
    private boolean active;

    public Puzzle() {
        this.active = true;
        this.difficultyTier = DifficultyTier.TIER_1;
        this.targetTimeSeconds = DifficultyTier.TIER_1.getDefaultTargetTimeSeconds();
        this.basePoints = DifficultyTier.TIER_1.getBasePoints();
    }

    public Puzzle(Long id, Long categoryId, PuzzleType puzzleType, DifficultyTier difficultyTier,
                  String title, String instruction, int targetTimeSeconds, int basePoints,
                  String puzzleDataJson, String solutionDataJson) {
        this.id = id;
        this.categoryId = categoryId;
        this.puzzleType = puzzleType;
        this.difficultyTier = difficultyTier != null ? difficultyTier : DifficultyTier.TIER_1;
        this.title = title;
        this.instruction = instruction;
        this.targetTimeSeconds = targetTimeSeconds > 0 ? targetTimeSeconds : this.difficultyTier.getDefaultTargetTimeSeconds();
        this.basePoints = basePoints > 0 ? basePoints : this.difficultyTier.getBasePoints();
        this.puzzleDataJson = puzzleDataJson;
        this.solutionDataJson = solutionDataJson;
        this.active = true;
    }

    /**
     * Converts this server-side domain model to a secure client-facing view,
     * stripping solutionDataJson to prevent answer leakage.
     */
    public PuzzleClientView toClientView() {
        return new PuzzleClientView(
                this.id,
                this.categoryId,
                this.puzzleType,
                this.difficultyTier,
                this.title,
                this.instruction,
                this.targetTimeSeconds,
                this.basePoints,
                this.puzzleDataJson
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public PuzzleType getPuzzleType() {
        return puzzleType;
    }

    public void setPuzzleType(PuzzleType puzzleType) {
        this.puzzleType = puzzleType;
    }

    public DifficultyTier getDifficultyTier() {
        return difficultyTier;
    }

    public void setDifficultyTier(DifficultyTier difficultyTier) {
        this.difficultyTier = difficultyTier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public int getTargetTimeSeconds() {
        return targetTimeSeconds;
    }

    public void setTargetTimeSeconds(int targetTimeSeconds) {
        this.targetTimeSeconds = targetTimeSeconds;
    }

    public int getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(int basePoints) {
        this.basePoints = basePoints;
    }

    public String getPuzzleDataJson() {
        return puzzleDataJson;
    }

    public void setPuzzleDataJson(String puzzleDataJson) {
        this.puzzleDataJson = puzzleDataJson;
    }

    public String getSolutionDataJson() {
        return solutionDataJson;
    }

    public void setSolutionDataJson(String solutionDataJson) {
        this.solutionDataJson = solutionDataJson;
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
        Puzzle puzzle = (Puzzle) o;
        return Objects.equals(id, puzzle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Puzzle{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", puzzleType=" + puzzleType +
                ", difficultyTier=" + difficultyTier +
                ", title='" + title + '\'' +
                ", active=" + active +
                '}';
    }
}
