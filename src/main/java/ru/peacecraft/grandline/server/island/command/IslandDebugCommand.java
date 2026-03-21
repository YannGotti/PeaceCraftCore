package ru.peacecraft.grandline.server.island.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import ru.peacecraft.grandline.server.island.model.IslandDefinition;
import ru.peacecraft.grandline.server.island.model.IslandEntryValidationResult;
import ru.peacecraft.grandline.server.island.service.IslandDiscoveryService;
import ru.peacecraft.grandline.server.island.service.IslandEntryValidationService;
import ru.peacecraft.grandline.server.island.service.IslandRegistry;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;
import ru.peacecraft.grandline.server.player.service.PlayerProfileService;

import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
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
                                        .executes(context -> executeMe(context.getSource())))
                                .then(literal("discover")
                                        .then(argument("id", StringArgumentType.word())
                                                .executes(context -> executeDiscover(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "id")
                                                ))))
                                .then(literal("unlock")
                                        .then(argument("id", StringArgumentType.word())
                                                .executes(context -> executeUnlock(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "id")
                                                ))))
                                .then(literal("validate")
                                        .then(argument("id", StringArgumentType.word())
                                                .executes(context -> executeValidate(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "id")
                                                ))))
                        )
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

    private static int executeDiscover(ServerCommandSource source, String islandId)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        if (!IslandRegistry.getInstance().contains(islandId)) {
            source.sendError(Text.literal("Island not found: " + islandId));
            return 0;
        }

        ServerPlayerEntity player = source.getPlayerOrThrow();
        PlayerProfileService profileService = PlayerProfileService.getInstance();
        PlayerProfile profile = profileService.getOrLoadProfile(source.getServer(), player);

        boolean changed = IslandDiscoveryService.getInstance().discoverIsland(profile, islandId);
        profileService.saveProfile(source.getServer(), profile);

        source.sendFeedback(
                () -> Text.literal("Discover island '" + islandId + "': changed=" + changed),
                false
        );

        return 1;
    }

    private static int executeUnlock(ServerCommandSource source, String islandId)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        if (!IslandRegistry.getInstance().contains(islandId)) {
            source.sendError(Text.literal("Island not found: " + islandId));
            return 0;
        }

        ServerPlayerEntity player = source.getPlayerOrThrow();
        PlayerProfileService profileService = PlayerProfileService.getInstance();
        PlayerProfile profile = profileService.getOrLoadProfile(source.getServer(), player);
        IslandDiscoveryService discoveryService = IslandDiscoveryService.getInstance();

        Set<String> missingRequirements = discoveryService.getMissingUnlockRequirements(profile, islandId);
        if (!missingRequirements.isEmpty()) {
            source.sendError(Text.literal(
                    "Cannot unlock '" + islandId + "'. Missing requirements: " + missingRequirements
            ));
            return 0;
        }

        boolean alreadyUnlocked = discoveryService.isUnlocked(profile, islandId);
        boolean changed = discoveryService.unlockIsland(profile, islandId);
        profileService.saveProfile(source.getServer(), profile);

        source.sendFeedback(
                () -> Text.literal(
                        "Unlock island '" + islandId + "': alreadyUnlocked=" + alreadyUnlocked + ", changed=" + changed
                ),
                false
        );

        return 1;
    }

    private static int executeValidate(ServerCommandSource source, String islandId)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PlayerProfile profile = PlayerProfileService.getInstance()
                .getOrLoadProfile(source.getServer(), player);

        IslandEntryValidationResult result = IslandEntryValidationService.getInstance()
                .validate(profile, islandId);

        source.sendFeedback(
                () -> Text.literal(
                        "Validate island '" + islandId + "': allowed=" + result.allowed()
                                + ", status=" + result.status()
                                + ", message=" + result.message()
                ),
                false
        );

        return result.allowed() ? 1 : 0;
    }
}