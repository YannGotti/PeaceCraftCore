package ru.peacecraft.grandline.common.item;

import java.util.function.Function;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import ru.peacecraft.grandline.common.constant.ModIds;
import ru.peacecraft.grandline.common.util.ModLog;

public final class ModItems {
    public static final Item LOG_POSE = register(
            "log_pose",
            LogPoseItem::new,
            new Item.Settings().maxCount(1)
    );

    private static boolean initialized = false;

    private ModItems() {
    }

    private static <T extends Item> T register(String name, Function<Item.Settings, T> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, ModIds.id(name));
        T item = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void initialize() {
        if (initialized) {
            return;
        }

        initialized = true;

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register(entries -> entries.add(LOG_POSE));

        ModLog.LOGGER.info("ModItems initialized.");
    }
}