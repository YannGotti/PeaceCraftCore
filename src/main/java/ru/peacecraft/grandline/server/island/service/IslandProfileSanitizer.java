package ru.peacecraft.grandline.server.island.service;

import ru.peacecraft.grandline.server.island.model.IslandDefinition;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;

import java.util.HashSet;

public final class IslandProfileSanitizer {
    private IslandProfileSanitizer() {
    }

    public static void sanitize(PlayerProfile profile) {
        IslandRegistry registry = IslandRegistry.getInstance();

        if (profile.getDiscoveredIslandIds() == null) {
            profile.setDiscoveredIslandIds(new HashSet<>());
        }

        if (profile.getUnlockedIslandIds() == null) {
            profile.setUnlockedIslandIds(new HashSet<>());
        }

        if (registry.isEmpty()) {
            return;
        }

        String starterIslandId = registry.getRequiredStarterIslandId();

        profile.getDiscoveredIslandIds().removeIf(id -> !registry.contains(id));
        profile.getUnlockedIslandIds().removeIf(id -> !registry.contains(id));

        if (profile.getCurrentIslandId() == null || !registry.contains(profile.getCurrentIslandId())) {
            profile.setCurrentIslandId(starterIslandId);
        }

        if (profile.getActiveLogPoseTargetId() != null && !registry.contains(profile.getActiveLogPoseTargetId())) {
            profile.setActiveLogPoseTargetId(null);
        }

        for (IslandDefinition definition : registry.getAll()) {
            if (definition.unlockedByDefault()) {
                profile.getDiscoveredIslandIds().add(definition.id());
                profile.getUnlockedIslandIds().add(definition.id());
            }
        }

        profile.getDiscoveredIslandIds().add(starterIslandId);
        profile.getUnlockedIslandIds().add(starterIslandId);

        profile.getDiscoveredIslandIds().add(profile.getCurrentIslandId());
        profile.getUnlockedIslandIds().add(profile.getCurrentIslandId());
    }
}