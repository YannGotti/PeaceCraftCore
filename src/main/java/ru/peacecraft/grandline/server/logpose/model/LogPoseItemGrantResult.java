package ru.peacecraft.grandline.server.logpose.model;

public record LogPoseItemGrantResult(
        boolean changedProfile,
        boolean gaveItem,
        LogPoseItemGrantStatus status,
        String message
) {
}