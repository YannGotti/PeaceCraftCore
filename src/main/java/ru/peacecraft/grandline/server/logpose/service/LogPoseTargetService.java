package ru.peacecraft.grandline.server.logpose.service;

import java.util.List;
import java.util.Objects;

import ru.peacecraft.grandline.server.island.model.IslandDefinition;
import ru.peacecraft.grandline.server.island.service.IslandDiscoveryService;
import ru.peacecraft.grandline.server.island.service.IslandRegistry;
import ru.peacecraft.grandline.server.logpose.model.LogPoseTargetSelectionResult;
import ru.peacecraft.grandline.server.logpose.model.LogPoseTargetSelectionStatus;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;

public final class LogPoseTargetService {
    private static final LogPoseTargetService INSTANCE = new LogPoseTargetService();

    private LogPoseTargetService() {
    }

    public static LogPoseTargetService getInstance() {
        return INSTANCE;
    }

    public List<String> getAvailableTargetIds(PlayerProfile profile) {
        IslandRegistry registry = IslandRegistry.getInstance();
        IslandDiscoveryService discoveryService = IslandDiscoveryService.getInstance();
        String currentIslandId = profile.getCurrentIslandId();

        return registry.getAll().stream()
                .map(IslandDefinition::id)
                .filter(id -> !Objects.equals(id, currentIslandId))
                .filter(id -> discoveryService.isDiscovered(profile, id))
                .sorted()
                .toList();
    }

    public boolean hasValidActiveTarget(PlayerProfile profile) {
        return getValidatedActiveTargetIdOrNull(profile) != null;
    }

    public String getValidatedActiveTargetIdOrNull(PlayerProfile profile) {
        IslandRegistry registry = IslandRegistry.getInstance();
        IslandDiscoveryService discoveryService = IslandDiscoveryService.getInstance();

        String targetIslandId = profile.getActiveLogPoseTargetId();
        if (targetIslandId == null || targetIslandId.isBlank()) {
            return null;
        }

        if (!registry.contains(targetIslandId)) {
            return null;
        }

        String currentIslandId = profile.getCurrentIslandId();
        if (currentIslandId == null || !registry.contains(currentIslandId)) {
            return null;
        }

        if (currentIslandId.equals(targetIslandId)) {
            return null;
        }

        if (!discoveryService.isDiscovered(profile, targetIslandId)) {
            return null;
        }

        return targetIslandId;
    }

    public LogPoseTargetSelectionResult selectTarget(PlayerProfile profile, String targetIslandId) {
        IslandRegistry registry = IslandRegistry.getInstance();
        IslandDiscoveryService discoveryService = IslandDiscoveryService.getInstance();

        if (targetIslandId == null || targetIslandId.isBlank() || !registry.contains(targetIslandId)) {
            return fail(
                    targetIslandId,
                    LogPoseTargetSelectionStatus.TARGET_NOT_FOUND,
                    "Log Pose target island not found: " + targetIslandId
            );
        }

        String currentIslandId = profile.getCurrentIslandId();
        if (currentIslandId == null || !registry.contains(currentIslandId)) {
            return fail(
                    targetIslandId,
                    LogPoseTargetSelectionStatus.CURRENT_ISLAND_NOT_FOUND,
                    "Current island is missing or invalid: " + currentIslandId
            );
        }

        if (currentIslandId.equals(targetIslandId)) {
            return fail(
                    targetIslandId,
                    LogPoseTargetSelectionStatus.TARGET_IS_CURRENT_ISLAND,
                    "Cannot set Log Pose target to current island: " + targetIslandId
            );
        }

        if (!discoveryService.isDiscovered(profile, targetIslandId)) {
            return fail(
                    targetIslandId,
                    LogPoseTargetSelectionStatus.TARGET_NOT_DISCOVERED,
                    "Cannot set Log Pose target to undiscovered island: " + targetIslandId
            );
        }

        if (targetIslandId.equals(profile.getActiveLogPoseTargetId())) {
            return fail(
                    targetIslandId,
                    LogPoseTargetSelectionStatus.TARGET_ALREADY_SELECTED,
                    "Log Pose target is already selected: " + targetIslandId
            );
        }

        profile.setActiveLogPoseTargetId(targetIslandId);

        return new LogPoseTargetSelectionResult(
                true,
                true,
                targetIslandId,
                LogPoseTargetSelectionStatus.SELECTED,
                "Log Pose target selected: " + targetIslandId
        );
    }

    public boolean clearTarget(PlayerProfile profile) {
        if (profile.getActiveLogPoseTargetId() == null) {
            return false;
        }

        profile.setActiveLogPoseTargetId(null);
        return true;
    }

    private LogPoseTargetSelectionResult fail(
            String targetIslandId,
            LogPoseTargetSelectionStatus status,
            String message
    ) {
        return new LogPoseTargetSelectionResult(false, false, targetIslandId, status, message);
    }
}