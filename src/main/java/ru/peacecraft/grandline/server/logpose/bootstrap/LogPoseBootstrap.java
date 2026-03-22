package ru.peacecraft.grandline.server.logpose.bootstrap;

import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.logpose.command.LogPoseCommand;

public final class LogPoseBootstrap {
    private static boolean initialized = false;

    private LogPoseBootstrap() {
    }

    public static void init() {
        if (initialized) {
            ModLog.LOGGER.warn("LogPoseBootstrap.init() called more than once. Skipping.");
            return;
        }

        initialized = true;

        LogPoseCommand.register();

        ModLog.LOGGER.info("LogPoseBootstrap initialized.");
    }
}