package ru.peacecraft.grandline.server.island.service;

import java.util.HashSet;

import ru.peacecraft.grandline.server.player.model.PlayerProfile;

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

        profile.getDiscoveredIslandIds().add(starterIslandId);
        profile.getUnlockedIslandIds().add(starterIslandId);
    }
}