package ru.peacecraft.grandline.server.island.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import ru.peacecraft.grandline.server.island.model.IslandDefinition;
import ru.peacecraft.grandline.server.island.service.IslandRegistry;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;
import ru.peacecraft.grandline.server.player.service.PlayerProfileService;

import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.literal;

public final class IslandDebugCommand {
    private static boolean registered = false;

    private IslandDebugCommand() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("pcdev")
                        .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS))
                        .then(literal("islands")
                                .then(literal("list")
                                        .executes(context -> executeList(context.getSource())))
                                .then(literal("starter")
                                        .executes(context -> executeStarter(context.getSource())))
                                .then(literal("me")
                                        .executes(context -> executeMe(context.getSource()))))
        );
    }

    private static int executeList(ServerCommandSource source) {
        IslandRegistry registry = IslandRegistry.getInstance();

        String joinedIds = registry.getAll().stream()
                .map(IslandDefinition::id)
                .collect(Collectors.joining(", "));

        source.sendFeedback(
                () -> Text.literal("Loaded islands (" + registry.getAll().size() + "): " + joinedIds),
                false
        );

        return 1;
    }

    private static int executeStarter(ServerCommandSource source) {
        IslandDefinition starter = IslandRegistry.getInstance().getStarterIsland()
                .orElseThrow(() -> new IllegalStateException("Starter island not found"));

        source.sendFeedback(
                () -> Text.literal("Starter island: " + starter.id() + " [" + starter.displayName() + "]"),
                false
        );

        return 1;
    }

    private static int executeMe(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();

        PlayerProfile profile = PlayerProfileService.getInstance()
                .getOrLoadProfile(source.getServer(), player);

        source.sendFeedback(
                () -> Text.literal(
                        "Current=" + profile.getCurrentIslandId()
                                + ", discovered=" + profile.getDiscoveredIslandIds()
                                + ", unlocked=" + profile.getUnlockedIslandIds()
                                + ", logPoseTarget=" + profile.getActiveLogPoseTargetId()
                ),
                false
        );

        return 1;
    }
}