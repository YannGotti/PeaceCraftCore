package ru.peacecraft.grandline.server.island.model;

import net.minecraft.util.Identifier;

import java.util.Set;

public record IslandDefinition(
        String id,
        String displayName,
        Identifier spawnDimension,
        IslandSpawnPoint spawnPoint,
        boolean starter,
        boolean unlockedByDefault,
        Set<String> requiredUnlockedIslandIds
) {
    public IslandDefinition {
        requiredUnlockedIslandIds = requiredUnlockedIslandIds == null
                ? Set.of()
                : Set.copyOf(requiredUnlockedIslandIds);
    }

    public boolean hasId(String islandId) {
        return id != null && id.equals(islandId);
    }

    public boolean hasUnlockRequirements() {
        return !requiredUnlockedIslandIds.isEmpty();
    }
}