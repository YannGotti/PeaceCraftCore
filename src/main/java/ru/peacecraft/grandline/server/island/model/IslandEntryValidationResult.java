package ru.peacecraft.grandline.server.island.model;

public record IslandEntryValidationResult(
        boolean allowed,
        IslandEntryValidationStatus status,
        String message
) {
}