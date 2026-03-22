package ru.peacecraft.grandline.server.logpose.model;

public record LogPoseTargetSelectionResult(
        boolean success,
        boolean changed,
        String targetIslandId,
        LogPoseTargetSelectionStatus status,
        String message
) {
}