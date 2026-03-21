package ru.peacecraft.grandline.server.island.model;

import net.minecraft.util.Identifier;

public record IslandDefinition(
        String id,
        String displayName,
        Identifier spawnDimension,
        IslandSpawnPoint spawnPoint,
        boolean starter,
        boolean unlockedByDefault
) {
    public boolean hasId(String islandId) {
        return id != null && id.equals(islandId);
    }
}