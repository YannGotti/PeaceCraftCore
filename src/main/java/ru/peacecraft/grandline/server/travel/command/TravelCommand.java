package ru.peacecraft.grandline.server.travel.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import ru.peacecraft.grandline.server.island.model.IslandEntryValidationResult;
import ru.peacecraft.grandline.server.player.model.PlayerProfile;
import ru.peacecraft.grandline.server.player.service.PlayerProfileService;
import ru.peacecraft.grandline.server.travel.model.TravelRequestOperationResult;
import ru.peacecraft.grandline.server.travel.service.TravelRequestService;

import static net.minecraft.server.command.CommandManager.literal;

public final class TravelCommand {
    private static boolean registered = false;

    private TravelCommand() {
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
                literal("travel")
                        .then(literal("request")
                                .executes(context -> executeRequest(context.getSource())))
                        .then(literal("status")
                                .executes(context -> executeStatus(context.getSource())))
                        .then(literal("cancel")
                                .executes(context -> executeCancel(context.getSource())))
        );
    }

    private static int executeRequest(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PlayerProfileService profileService = PlayerProfileService.getInstance();
        PlayerProfile profile = profileService.getOrLoadProfile(source.getServer(), player);

        TravelRequestOperationResult result = TravelRequestService.getInstance()
                .createRequestFromActiveLogPose(profile);

        if (!result.success()) {
            source.sendError(Text.literal(
                    "Travel request failed: status=" + result.status()
                            + ", target=" + result.targetIslandId()
                            + ", entryStatus=" + result.entryValidationStatus()
                            + ", message=" + result.message()
            ));
            return 0;
        }

        if (result.changed()) {
            profileService.saveProfile(source.getServer(), profile);
        }

        source.sendFeedback(
                () -> Text.literal(
                        "Travel request ok: status=" + result.status()
                                + ", target=" + result.targetIslandId()
                                + ", entryStatus=" + result.entryValidationStatus()
                ),
                false
        );

        return 1;
    }

    private static int executeStatus(ServerCommandSource source) throws CommandSyntaxException {
        PlayerProfile profile = getProfile(source);
        TravelRequestService travelRequestService = TravelRequestService.getInstance();

        if (!travelRequestService.hasActiveRequest(profile)) {
            String activeLogPoseTarget = profile.getActiveLogPoseTargetId();
            source.sendFeedback(
                    () -> Text.literal(
                            "Travel request: none. ActiveLogPoseTarget="
                                    + (activeLogPoseTarget == null ? "none" : activeLogPoseTarget)
                    ),
                    false
            );
            return 1;
        }

        String requestedTargetId = profile.getRequestedTravelTargetId();
        long ageSeconds = Math.max(0L,
                (System.currentTimeMillis() - profile.getTravelRequestCreatedAtEpochMs()) / 1000L
        );

        IslandEntryValidationResult entryResult =
                ru.peacecraft.grandline.server.island.service.IslandEntryValidationService.getInstance()
                        .validate(profile, requestedTargetId);

        source.sendFeedback(
                () -> Text.literal(
                        "Travel request: status=" + profile.getTravelRequestStatus()
                                + ", target=" + requestedTargetId
                                + ", ageSeconds=" + ageSeconds
                                + ", entryAllowed=" + entryResult.allowed()
                                + ", entryStatus=" + entryResult.status()
                ),
                false
        );

        return 1;
    }

    private static int executeCancel(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PlayerProfileService profileService = PlayerProfileService.getInstance();
        PlayerProfile profile = profileService.getOrLoadProfile(source.getServer(), player);

        TravelRequestOperationResult result = TravelRequestService.getInstance()
                .cancelRequest(profile);

        if (!result.success()) {
            source.sendError(Text.literal(
                    "Travel cancel failed: status=" + result.status()
                            + ", message=" + result.message()
            ));
            return 0;
        }

        if (result.changed()) {
            profileService.saveProfile(source.getServer(), profile);
        }

        source.sendFeedback(
                () -> Text.literal(
                        "Travel request cancelled: target=" + result.targetIslandId()
                ),
                false
        );

        return 1;
    }

    private static PlayerProfile getProfile(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        return PlayerProfileService.getInstance().getOrLoadProfile(source.getServer(), player);
    }
}