package ru.peacecraft.grandline;

import net.fabricmc.api.ModInitializer;
import ru.peacecraft.grandline.common.registry.CommonBootstrap;
import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.bootstrap.ServerBootstrap;

public final class PeaceCraftMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ModLog.LOGGER.info("===== PeaceCraft mod initialization started =====");
        CommonBootstrap.init();
        ServerBootstrap.init();
        ModLog.LOGGER.info("===== PeaceCraft mod initialization finished =====");
    }
}