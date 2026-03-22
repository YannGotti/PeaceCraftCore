package ru.peacecraft.grandline.server.island.service;

import java.util.HashSet;

import ru.peacecraft.grandline.server.island.model.IslandDefinition;
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

        sanitizeActiveLogPoseTarget(profile, registry);
    }

    private static void sanitizeActiveLogPoseTarget(PlayerProfile profile, IslandRegistry registry) {
        String activeTargetId = profile.getActiveLogPoseTargetId();
        if (activeTargetId == null) {
            return;
        }

        if (!registry.contains(activeTargetId)) {
            profile.setActiveLogPoseTargetId(null);
            return;
        }

        if (activeTargetId.equals(profile.getCurrentIslandId())) {
            profile.setActiveLogPoseTargetId(null);
            return;
        }

        if (!profile.getDiscoveredIslandIds().contains(activeTargetId)) {
            profile.setActiveLogPoseTargetId(null);
        }
    }
}