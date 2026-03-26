package ru.peacecraft.grandline.server.travel.model;

import ru.peacecraft.grandline.server.island.model.IslandEntryValidationStatus;

public record TravelRequestOperationResult(
        boolean success,
        boolean changed,
        TravelRequestOperationStatus status,
        String targetIslandId,
        IslandEntryValidationStatus entryValidationStatus,
        String message
) {
}