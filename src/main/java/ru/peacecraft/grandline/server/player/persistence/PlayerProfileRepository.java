package ru.peacecraft.grandline.server.player.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;

public final class PlayerProfileRepository {
    private final Gson gson;

    public PlayerProfileRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public Optional<PlayerProfile> load(Path serverRoot, UUID playerUuid) {
        Path profilePath = PlayerProfilePaths.getProfilePath(serverRoot, playerUuid);

        if (!Files.exists(profilePath)) {
            return Optional.empty();
        }

        try (Reader reader = Files.newBufferedReader(profilePath)) {
            PlayerProfile profile = gson.fromJson(reader, PlayerProfile.class);
            return Optional.ofNullable(profile);
        } catch (IOException e) {
            ModLog.LOGGER.error("Failed to load player profile for UUID {}", playerUuid, e);
            return Optional.empty();
        }
    }

    public void save(Path serverRoot, PlayerProfile profile) {
        Path profilesDirectory = PlayerProfilePaths.getProfilesDirectory(serverRoot);
        Path profilePath = PlayerProfilePaths.getProfilePath(serverRoot, profile.getPlayerUuid());

        try {
            Files.createDirectories(profilesDirectory);

            try (Writer writer = Files.newBufferedWriter(profilePath)) {
                gson.toJson(profile, writer);
            }
        } catch (IOException e) {
            ModLog.LOGGER.error("Failed to save player profile for UUID {}", profile.getPlayerUuid(), e);
        }
    }
}