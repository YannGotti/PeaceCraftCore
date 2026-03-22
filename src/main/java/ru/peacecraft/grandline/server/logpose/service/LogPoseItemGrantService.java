package ru.peacecraft.grandline.server.logpose.service;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.peacecraft.grandline.common.item.ModItems;
import ru.peacecraft.grandline.server.logpose.model.LogPoseItemGrantResult;
import ru.peacecraft.grandline.server.logpose.model.LogPoseItemGrantStatus;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;

public final class LogPoseItemGrantService {
    private static final LogPoseItemGrantService INSTANCE = new LogPoseItemGrantService();

    private LogPoseItemGrantService() {
    }

    public static LogPoseItemGrantService getInstance() {
        return INSTANCE;
    }

    public boolean hasLogPoseInInventory(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(ModItems.LOG_POSE)) {
                return true;
            }
        }
        return false;
    }

    public LogPoseItemGrantResult grantStarterIfNeeded(ServerPlayerEntity player, PlayerProfile profile) {
        if (profile.isStarterLogPoseGranted()) {
            return new LogPoseItemGrantResult(
                    false,
                    false,
                    LogPoseItemGrantStatus.ALREADY_GRANTED_ON_FIRST_JOIN,
                    "Starter Log Pose was already processed earlier."
            );
        }

        if (hasLogPoseInInventory(player)) {
            profile.setStarterLogPoseGranted(true);

            return new LogPoseItemGrantResult(
                    true,
                    false,
                    LogPoseItemGrantStatus.MARKED_GRANTED_EXISTING_ITEM,
                    "Player already had a Log Pose. Starter grant marked as completed."
            );
        }

        ItemStack stack = new ItemStack(ModItems.LOG_POSE);
        boolean inserted = player.getInventory().insertStack(stack);

        profile.setStarterLogPoseGranted(true);

        if (inserted && stack.isEmpty()) {
            return new LogPoseItemGrantResult(
                    true,
                    true,
                    LogPoseItemGrantStatus.GIVEN_TO_INVENTORY,
                    "You received a Log Pose."
            );
        }

        if (!stack.isEmpty()) {
            player.dropItem(stack, false);
        }

        return new LogPoseItemGrantResult(
                true,
                true,
                LogPoseItemGrantStatus.DROPPED_NEAR_PLAYER,
                "You received a Log Pose. Your inventory was full, so it was dropped near you."
        );
    }

    public LogPoseItemGrantResult grantByCommandIfAbsent(ServerPlayerEntity player) {
        if (hasLogPoseInInventory(player)) {
            return new LogPoseItemGrantResult(
                    false,
                    false,
                    LogPoseItemGrantStatus.ALREADY_HAS_IN_INVENTORY,
                    "You already have a Log Pose in your inventory."
            );
        }

        ItemStack stack = new ItemStack(ModItems.LOG_POSE);
        boolean inserted = player.getInventory().insertStack(stack);

        if (inserted && stack.isEmpty()) {
            return new LogPoseItemGrantResult(
                    false,
                    true,
                    LogPoseItemGrantStatus.GIVEN_TO_INVENTORY,
                    "You received a Log Pose."
            );
        }

        if (!stack.isEmpty()) {
            player.dropItem(stack, false);
        }

        return new LogPoseItemGrantResult(
                false,
                true,
                LogPoseItemGrantStatus.DROPPED_NEAR_PLAYER,
                "Your inventory was full, so the Log Pose was dropped near you."
        );
    }
}