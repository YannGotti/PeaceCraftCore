package ru.peacecraft.grandline.server.player.service;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.island.service.IslandProfileSanitizer;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;
import ru.peacecraft.grandline.server.player.persistence.PlayerProfileRepository;
import ru.peacecraft.grandline.server.travel.service.TravelProfileSanitizer;

public final class PlayerProfileService {
    private static final PlayerProfileService INSTANCE = new PlayerProfileService();

    private final Map<UUID, PlayerProfile> loadedProfiles = new ConcurrentHashMap<>();
    private final PlayerProfileRepository repository = new PlayerProfileRepository();

    private PlayerProfileService() {
    }

    public static PlayerProfileService getInstance() {
        return INSTANCE;
    }

    public PlayerProfile getOrLoadProfile(MinecraftServer server, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        PlayerProfile cached = loadedProfiles.get(uuid);
        if (cached != null) {
            return cached;
        }

        Path serverRoot = server.getRunDirectory();

        Optional<PlayerProfile> loaded = repository.load(serverRoot, uuid);
        PlayerProfile profile = loaded.orElseGet(() -> {
            ModLog.LOGGER.info("Creating new player profile for {}", player.getName().getString());
            return PlayerProfile.createDefault(uuid, player.getName().getString());
        });

        profile.setLastKnownName(player.getName().getString());

        IslandProfileSanitizer.sanitize(profile);
        TravelProfileSanitizer.sanitize(profile);

        profile.touch();

        loadedProfiles.put(uuid, profile);
        return profile;
    }

    public Optional<PlayerProfile> getLoadedProfile(UUID uuid) {
        return Optional.ofNullable(loadedProfiles.get(uuid));
    }

    public void saveProfile(MinecraftServer server, PlayerProfile profile) {
        profile.touch();
        repository.save(server.getRunDirectory(), profile);
    }

    public void saveAndUnloadProfile(MinecraftServer server, UUID uuid) {
        PlayerProfile profile = loadedProfiles.remove(uuid);
        if (profile != null) {
            saveProfile(server, profile);
        }
    }

    public void saveAll(MinecraftServer server) {
        for (PlayerProfile profile : loadedProfiles.values()) {
            saveProfile(server, profile);
        }
    }
}