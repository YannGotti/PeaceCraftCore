package ru.peacecraft.grandline.server.island.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.island.model.IslandDefinition;
import ru.peacecraft.grandline.server.island.model.IslandSpawnPoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class IslandDefinitionLoader {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private IslandDefinitionLoader() {
    }

    public static List<IslandDefinition> loadAll(ResourceManager resourceManager) {
        Map<Identifier, Resource> resources = resourceManager.findResources(
                "peacecraft/islands",
                id -> id.getPath().endsWith(".json")
        );

        List<IslandDefinition> result = new ArrayList<>();

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            Resource resource = entry.getValue();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            )) {
                IslandDefinitionJson json = GSON.fromJson(reader, IslandDefinitionJson.class);
                result.add(toDefinition(resourceId, json));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load island definition from " + resourceId, e);
            }
        }

        ModLog.LOGGER.info("Discovered {} island json resource(s)", result.size());
        return result;
    }

    private static IslandDefinition toDefinition(Identifier resourceId, IslandDefinitionJson json) {
        if (json == null) {
            throw new IllegalStateException("Island json is null: " + resourceId);
        }

        if (isBlank(json.id)) {
            throw new IllegalStateException("Island id is blank in " + resourceId);
        }

        if (isBlank(json.displayName)) {
            throw new IllegalStateException("Island displayName is blank in " + resourceId);
        }

        if (isBlank(json.spawnDimension)) {
            throw new IllegalStateException("Island spawnDimension is blank in " + resourceId);
        }

        if (json.spawn == null) {
            throw new IllegalStateException("Island spawn block is missing in " + resourceId);
        }

        Identifier dimensionId;
        try {
            dimensionId = Identifier.of(json.spawnDimension);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid spawnDimension '" + json.spawnDimension + "' in " + resourceId, e);
        }

        Set<String> requiredUnlockedIslandIds = normalizeRequiredUnlockedIslandIds(
                json.requiredUnlockedIslandIds,
                resourceId
        );

        IslandSpawnPoint spawnPoint = new IslandSpawnPoint(
                json.spawn.x,
                json.spawn.y,
                json.spawn.z,
                json.spawn.yaw,
                json.spawn.pitch
        );

        return new IslandDefinition(
                json.id,
                json.displayName,
                dimensionId,
                spawnPoint,
                json.starter,
                json.unlockedByDefault,
                requiredUnlockedIslandIds
        );
    }

    private static Set<String> normalizeRequiredUnlockedIslandIds(Set<String> rawIds, Identifier resourceId) {
        if (rawIds == null || rawIds.isEmpty()) {
            return Set.of();
        }

        Set<String> normalized = new LinkedHashSet<>();

        for (String islandId : rawIds) {
            if (isBlank(islandId)) {
                throw new IllegalStateException("Blank requiredUnlockedIslandIds entry in " + resourceId);
            }

            normalized.add(islandId);
        }

        return normalized;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}