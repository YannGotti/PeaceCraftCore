package ru.peacecraft.grandline.server.island.service;

import ru.peacecraft.grandline.server.island.model.IslandDefinition;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class IslandDiscoveryService {
    private static final IslandDiscoveryService INSTANCE = new IslandDiscoveryService();

    private IslandDiscoveryService() {
    }

    public static IslandDiscoveryService getInstance() {
        return INSTANCE;
    }

    public boolean isDiscovered(PlayerProfile profile, String islandId) {
        return profile.getDiscoveredIslandIds() != null
                && profile.getDiscoveredIslandIds().contains(islandId);
    }

    public boolean isUnlocked(PlayerProfile profile, String islandId) {
        return profile.getUnlockedIslandIds() != null
                && profile.getUnlockedIslandIds().contains(islandId);
    }

    public boolean discoverIsland(PlayerProfile profile, String islandId) {
        requireExistingIsland(islandId);
        return profile.getDiscoveredIslandIds().add(islandId);
    }

    public Set<String> getMissingUnlockRequirements(PlayerProfile profile, String islandId) {
        IslandDefinition definition = IslandRegistry.getInstance().getRequired(islandId);
        Set<String> missing = new LinkedHashSet<>();

        for (String requiredIslandId : definition.requiredUnlockedIslandIds()) {
            if (!isUnlocked(profile, requiredIslandId)) {
                missing.add(requiredIslandId);
            }
        }

        return Collections.unmodifiableSet(missing);
    }

    public boolean canUnlock(PlayerProfile profile, String islandId) {
        requireExistingIsland(islandId);
        return getMissingUnlockRequirements(profile, islandId).isEmpty();
    }

    public boolean unlockIsland(PlayerProfile profile, String islandId) {
        requireExistingIsland(islandId);

        if (!canUnlock(profile, islandId)) {
            return false;
        }

        profile.getDiscoveredIslandIds().add(islandId);
        return profile.getUnlockedIslandIds().add(islandId);
    }

    private void requireExistingIsland(String islandId) {
        if (!IslandRegistry.getInstance().contains(islandId)) {
            throw new IllegalStateException("Island not found: " + islandId);
        }
    }
}