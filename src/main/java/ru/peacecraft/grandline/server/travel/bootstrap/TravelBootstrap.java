package ru.peacecraft.grandline.server.travel.bootstrap;

import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.travel.command.TravelCommand;

public final class TravelBootstrap {
    private static boolean initialized = false;

    private TravelBootstrap() {
    }

    public static void init() {
        if (initialized) {
            ModLog.LOGGER.warn("TravelBootstrap.init() called more than once. Skipping.");
            return;
        }

        initialized = true;

        TravelCommand.register();

        ModLog.LOGGER.info("TravelBootstrap initialized.");
    }
}