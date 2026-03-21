package ru.peacecraft.grandline.server.island.service;

import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.island.model.IslandDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class IslandRegistry {
    private static final IslandRegistry INSTANCE = new IslandRegistry();

    private final Map<String, IslandDefinition> islands = new LinkedHashMap<>();

    private IslandRegistry() {
    }

    public static IslandRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized void replaceAll(Collection<IslandDefinition> definitions) {
        Map<String, IslandDefinition> next = new LinkedHashMap<>();

        for (IslandDefinition definition : definitions) {
            if (definition == null) {
                continue;
            }

            String id = definition.id();
            if (id == null || id.isBlank()) {
                throw new IllegalStateException("Encountered island definition with blank id");
            }

            if (next.containsKey(id)) {
                throw new IllegalStateException("Duplicate island id detected: " + id);
            }

            next.put(id, definition);
        }

        long starterCount = next.values().stream()
                .filter(IslandDefinition::starter)
                .count();

        if (starterCount != 1) {
            throw new IllegalStateException("Exactly one starter island is required, but found " + starterCount);
        }

        for (IslandDefinition definition : next.values()) {
            if (definition.starter() && definition.hasUnlockRequirements()) {
                throw new IllegalStateException(
                        "Starter island must not have unlock requirements: " + definition.id()
                );
            }

            for (String requiredIslandId : definition.requiredUnlockedIslandIds()) {
                if (requiredIslandId.equals(definition.id())) {
                    throw new IllegalStateException(
                            "Island cannot require itself for unlock: " + definition.id()
                    );
                }

                if (!next.containsKey(requiredIslandId)) {
                    throw new IllegalStateException(
                            "Island '" + definition.id()
                                    + "' requires unknown island '" + requiredIslandId + "'"
                    );
                }
            }
        }

        islands.clear();
        islands.putAll(next);

        ModLog.LOGGER.info(
                "IslandRegistry loaded {} island(s). Starter={}",
                islands.size(),
                getStarterIsland().map(IslandDefinition::id).orElse("none")
        );
    }

    public synchronized Optional<IslandDefinition> get(String islandId) {
        return Optional.ofNullable(islands.get(islandId));
    }

    public synchronized IslandDefinition getRequired(String islandId) {
        IslandDefinition definition = islands.get(islandId);
        if (definition == null) {
            throw new IllegalStateException("Island not found: " + islandId);
        }
        return definition;
    }

    public synchronized boolean contains(String islandId) {
        return islands.containsKey(islandId);
    }

    public synchronized Collection<IslandDefinition> getAll() {
        return Collections.unmodifiableCollection(islands.values());
    }

    public synchronized Optional<IslandDefinition> getStarterIsland() {
        return islands.values().stream()
                .filter(IslandDefinition::starter)
                .findFirst();
    }

    public synchronized String getRequiredStarterIslandId() {
        return getStarterIsland()
                .map(IslandDefinition::id)
                .orElseThrow(() -> new IllegalStateException("Starter island is not loaded"));
    }

    public synchronized boolean isEmpty() {
        return islands.isEmpty();
    }
}