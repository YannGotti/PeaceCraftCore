package ru.peacecraft.grandline;

import net.fabricmc.api.ClientModInitializer;
import ru.peacecraft.grandline.client.bootstrap.ClientBootstrap;
import ru.peacecraft.grandline.common.util.ModLog;

public final class PeaceCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModLog.LOGGER.info("===== PeaceCraft client initialization started =====");
        ClientBootstrap.init();
        ModLog.LOGGER.info("===== PeaceCraft client initialization finished =====");
    }
}