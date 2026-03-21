package ru.peacecraft.grandline.server.island.service;

import ru.peacecraft.grandline.server.island.model.IslandEntryValidationResult;
import ru.peacecraft.grandline.server.island.model.IslandEntryValidationStatus;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;

public final class IslandEntryValidationService {
    private static final IslandEntryValidationService INSTANCE = new IslandEntryValidationService();

    private IslandEntryValidationService() {
    }

    public static IslandEntryValidationService getInstance() {
        return INSTANCE;
    }

    public IslandEntryValidationResult validate(PlayerProfile profile, String targetIslandId) {
        IslandRegistry registry = IslandRegistry.getInstance();
        IslandDiscoveryService discoveryService = IslandDiscoveryService.getInstance();

        if (targetIslandId == null || targetIslandId.isBlank() || !registry.contains(targetIslandId)) {
            return fail(
                    IslandEntryValidationStatus.TARGET_ISLAND_NOT_FOUND,
                    "Target island does not exist: " + targetIslandId
            );
        }

        String currentIslandId = profile.getCurrentIslandId();
        if (currentIslandId == null || !registry.contains(currentIslandId)) {
            return fail(
                    IslandEntryValidationStatus.CURRENT_ISLAND_NOT_FOUND,
                    "Current island is missing or invalid: " + currentIslandId
            );
        }

        if (currentIslandId.equals(targetIslandId)) {
            return fail(
                    IslandEntryValidationStatus.ALREADY_ON_TARGET_ISLAND,
                    "Player is already on island: " + targetIslandId
            );
        }

        if (!discoveryService.isDiscovered(profile, targetIslandId)) {
            return fail(
                    IslandEntryValidationStatus.TARGET_NOT_DISCOVERED,
                    "Target island is not discovered yet: " + targetIslandId
            );
        }

        if (!discoveryService.isUnlocked(profile, targetIslandId)) {
            return fail(
                    IslandEntryValidationStatus.TARGET_NOT_UNLOCKED,
                    "Target island is not unlocked yet: " + targetIslandId
            );
        }

        return new IslandEntryValidationResult(
                true,
                IslandEntryValidationStatus.ALLOWED,
                "Entry allowed for island: " + targetIslandId
        );
    }

    private IslandEntryValidationResult fail(IslandEntryValidationStatus status, String message) {
        return new IslandEntryValidationResult(false, status, message);
    }
}