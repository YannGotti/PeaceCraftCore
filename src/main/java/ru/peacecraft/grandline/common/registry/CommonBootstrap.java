package ru.peacecraft.grandline.common.registry;

import ru.peacecraft.grandline.common.util.ModLog;

public final class CommonBootstrap {
    private static boolean initialized = false;

    private CommonBootstrap() {
    }

    public static void init() {
        if (initialized) {
            ModLog.LOGGER.warn("CommonBootstrap.init() called more than once. Skipping.");
            return;
        }

        initialized = true;
        ModLog.LOGGER.info("Initializing common PeaceCraft systems...");

        // Future:
        // - item registry bootstrap
        // - block registry bootstrap
        // - sound registry bootstrap
        // - shared codecs / serializers
        // - content definition loaders
    }
}