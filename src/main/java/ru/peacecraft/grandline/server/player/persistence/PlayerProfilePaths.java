package ru.peacecraft.grandline.server.player.persistence;

import java.nio.file.Path;
import java.util.UUID;

public final class PlayerProfilePaths {
    private PlayerProfilePaths() {
    }

    public static Path getProfilesDirectory(Path serverRoot) {
        return serverRoot.resolve("peacecraft").resolve("player_profiles");
    }

    public static Path getProfilePath(Path serverRoot, UUID playerUuid) {
        return getProfilesDirectory(serverRoot).resolve(playerUuid.toString() + ".json");
    }
}