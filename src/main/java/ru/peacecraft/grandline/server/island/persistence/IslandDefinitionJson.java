package ru.peacecraft.grandline.server.island.persistence;

public final class IslandDefinitionJson {
    public String id;
    public String displayName;
    public String spawnDimension;
    public SpawnJson spawn;
    public boolean starter;
    public boolean unlockedByDefault;

    public static final class SpawnJson {
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;
    }
}