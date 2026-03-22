package ru.peacecraft.grandline.server.logpose.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import ru.peacecraft.grandline.server.logpose.model.LogPoseItemGrantResult;
import ru.peacecraft.grandline.server.logpose.model.LogPoseItemGrantStatus;
import ru.peacecraft.grandline.server.logpose.service.LogPoseItemGrantService;

import static net.minecraft.server.command.CommandManager.literal;

public final class LogPoseCommand {
    private static boolean registered = false;

    private LogPoseCommand() {
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
                literal("logpose")
                        .executes(context -> executeGive(context.getSource()))
        );
    }

    private static int executeGive(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();

        LogPoseItemGrantResult result = LogPoseItemGrantService.getInstance()
                .grantByCommandIfAbsent(player);

        if (result.status() == LogPoseItemGrantStatus.ALREADY_HAS_IN_INVENTORY) {
            source.sendError(Text.literal(result.message()));
            return 0;
        }

        source.sendFeedback(() -> Text.literal(result.message()), false);
        return 1;
    }
}