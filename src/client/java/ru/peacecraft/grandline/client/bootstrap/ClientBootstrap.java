package ru.peacecraft.grandline.client.bootstrap;

import ru.peacecraft.grandline.common.util.ModLog;

public final class ClientBootstrap {
    private static boolean initialized = false;

    private ClientBootstrap() {
    }

    public static void init() {
        if (initialized) {
            ModLog.LOGGER.warn("ClientBootstrap.init() called more than once. Skipping.");
            return;
        }

        initialized = true;
        ModLog.LOGGER.info("Initializing client PeaceCraft systems...");
    }
}