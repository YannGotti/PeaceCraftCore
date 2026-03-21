package ru.peacecraft.grandline.server.island.bootstrap;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.island.command.IslandDebugCommand;
import ru.peacecraft.grandline.server.island.lifecycle.IslandDataReloadListener;

public final class IslandBootstrap {
    private static boolean initialized = false;

    private IslandBootstrap() {
    }

    public static void init() {
        if (initialized) {
            ModLog.LOGGER.warn("IslandBootstrap.init() called more than once. Skipping.");
            return;
        }

        initialized = true;

        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(new IslandDataReloadListener());

        IslandDebugCommand.register();

        ModLog.LOGGER.info("IslandBootstrap initialized.");
    }
}