package ru.peacecraft.grandline.common.item;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import ru.peacecraft.grandline.server.logpose.model.LogPoseTargetSelectionResult;
import ru.peacecraft.grandline.server.logpose.service.LogPoseTargetService;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;
import ru.peacecraft.grandline.server.player.service.PlayerProfileService;

public final class LogPoseItem extends Item {

    public LogPoseItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!(user instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }

        MinecraftServer server = world.getServer();
        if (server == null) {
            return ActionResult.PASS;
        }

        PlayerProfileService profileService = PlayerProfileService.getInstance();
        PlayerProfile profile = profileService.getOrLoadProfile(server, serverPlayer);
        LogPoseTargetService targetService = LogPoseTargetService.getInstance();

        if (user.isSneaking()) {
            boolean changed = targetService.clearTarget(profile);

            if (changed) {
                profileService.saveProfile(server, profile);
                serverPlayer.sendMessage(
                        Text.literal("Log Pose target cleared.").formatted(Formatting.YELLOW),
                        true
                );
            } else {
                serverPlayer.sendMessage(
                        Text.literal("Log Pose target is already empty.").formatted(Formatting.GRAY),
                        true
                );
            }

            return ActionResult.SUCCESS;
        }

        List<String> availableTargets = targetService.getAvailableTargetIds(profile);
        if (availableTargets.isEmpty()) {
            serverPlayer.sendMessage(
                    Text.literal("No available Log Pose targets.").formatted(Formatting.RED),
                    true
            );
            return ActionResult.SUCCESS;
        }

        String currentTargetId = profile.getActiveLogPoseTargetId();
        String nextTargetId = selectNextTarget(availableTargets, currentTargetId);

        LogPoseTargetSelectionResult result = targetService.selectTarget(profile, nextTargetId);
        if (result.success()) {
            profileService.saveProfile(server, profile);
            serverPlayer.sendMessage(
                    Text.literal("Log Pose target: " + result.targetIslandId()).formatted(Formatting.AQUA),
                    true
            );
        } else {
            serverPlayer.sendMessage(
                    Text.literal("Failed to set Log Pose target: " + result.message()).formatted(Formatting.RED),
                    true
            );
        }

        return ActionResult.SUCCESS;
    }

    private String selectNextTarget(List<String> availableTargets, String currentTargetId) {
        if (currentTargetId == null || !availableTargets.contains(currentTargetId)) {
            return availableTargets.getFirst();
        }

        int currentIndex = availableTargets.indexOf(currentTargetId);
        int nextIndex = (currentIndex + 1) % availableTargets.size();
        return availableTargets.get(nextIndex);
    }

    @Override
    public void appendTooltip(
            ItemStack stack,
            Item.TooltipContext context,
            TooltipDisplayComponent displayComponent,
            Consumer<Text> textConsumer,
            TooltipType type
    ) {
        textConsumer.accept(Text.literal("Right click: cycle target").formatted(Formatting.GRAY));
        textConsumer.accept(Text.literal("Shift + Right click: clear target").formatted(Formatting.DARK_GRAY));
    }
}