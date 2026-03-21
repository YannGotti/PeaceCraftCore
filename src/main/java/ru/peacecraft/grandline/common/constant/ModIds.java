package ru.peacecraft.grandline.common.constant;

import net.minecraft.util.Identifier;

public final class ModIds {
    public static final String MOD_ID = "peacecraft";

    private ModIds() {
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}