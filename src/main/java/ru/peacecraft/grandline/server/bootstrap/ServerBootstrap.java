package ru.peacecraft.grandline.server.bootstrap;

import ru.peacecraft.grandline.common.util.ModLog;

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
    }
}