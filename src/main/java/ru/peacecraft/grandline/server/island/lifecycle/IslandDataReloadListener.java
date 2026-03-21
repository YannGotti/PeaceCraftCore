package ru.peacecraft.grandline.server.island.lifecycle;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import ru.peacecraft.grandline.common.constant.ModIds;
import ru.peacecraft.grandline.common.util.ModLog;
import ru.peacecraft.grandline.server.island.model.IslandDefinition;
import ru.peacecraft.grandline.server.island.persistence.IslandDefinitionLoader;
import ru.peacecraft.grandline.server.island.service.IslandRegistry;

import java.util.List;

public final class IslandDataReloadListener implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return ModIds.id("island_data_reload");
    }

    @Override
    public void reload(ResourceManager manager) {
        try {
            List<IslandDefinition> loadedDefinitions = IslandDefinitionLoader.loadAll(manager);
            IslandRegistry.getInstance().replaceAll(loadedDefinitions);
            ModLog.LOGGER.info("Island data reload completed successfully.");
        } catch (Exception e) {
            ModLog.LOGGER.error("Island data reload failed.", e);
        }
    }
}