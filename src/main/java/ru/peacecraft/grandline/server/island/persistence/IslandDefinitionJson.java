package ru.peacecraft.grandline.server.island.persistence;

import java.util.Set;

public final class IslandDefinitionJson {
    public String id;
    public String displayName;
    public String spawnDimension;
    public SpawnJson spawn;
    public boolean starter;
    public boolean unlockedByDefault;
    public Set<String> requiredUnlockedIslandIds;

    public static final class SpawnJson {
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;
    }
}