package ru.peacecraft.grandline.server.bootstrap;

import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.island.bootstrap.IslandBootstrap;
import ru.peacecraft.grandline.server.logpose.bootstrap.LogPoseBootstrap;
import ru.peacecraft.grandline.server.player.lifecycle.PlayerProfileLifecycle;

public final class ServerBootstrap {
    private static boolean initialized = false;

    private ServerBootstrap() {
    }

    public static void init() {
        if (initialized) {
            ModLog.LOGGER.warn("ServerBootstrap.init() called more than once. Skipping.");
            return;
        }

        initialized = true;
        ModLog.LOGGER.info("Initializing server PeaceCraft systems...");

        IslandBootstrap.init();
        LogPoseBootstrap.init();
        PlayerProfileLifecycle.init();
    }
}