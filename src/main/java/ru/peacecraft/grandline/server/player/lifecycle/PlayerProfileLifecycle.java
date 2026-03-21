package ru.peacecraft.grandline.server.player.lifecycle;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.player.service.PlayerProfileService;

public final class PlayerProfileLifecycle {
    private static boolean initialized = false;

    private PlayerProfileLifecycle() {
    }

    public static void init() {
        if (initialized) {
            ModLog.LOGGER.warn("PlayerProfileLifecycle.init() called more than once. Skipping.");
            return;
        }

        initialized = true;

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerProfileService.getInstance().getOrLoadProfile(server, handler.getPlayer());
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerProfileService.getInstance().saveAndUnloadProfile(server, handler.getPlayer().getUuid());
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ModLog.LOGGER.info("Server stopping: saving all player profiles...");
            PlayerProfileService.getInstance().saveAll(server);
        });
    }
}