package io.github.brainage04.twitchplaysminecraft.command.move;

import io.github.brainage04.twitchplaysminecraft.command.ReleaseAllKeysCommand;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.MoveDirection;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.BlockPos;

public class MoveCommand {
    private static boolean isRunning = false;
    private static BlockPos startPos = null;
    private static int distance = 0;

    public static int stop(FabricClientCommandSource source) {
        ReleaseAllKeysCommand.execute(source);

        isRunning = false;
        startPos = null;
        distance = 0;

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Finished movement.")
                .execute();

        return 0;
    }

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (startPos == null) return;
            if (distance <= 0) return;
            if (client.player == null) return;
            if (startPos.getSquaredDistance(client.player.getBlockPos()) < distance * distance) return;

            stop(SourceUtils.getSource(client.player));
        });
    }

    public static int executeTime(FabricClientCommandSource source, String movementDirectionString, int amount) {
        ClientPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        MoveDirection moveDirection = EnumUtils.getValueSafely(MoveDirection.class, movementDirectionString);
        if (moveDirection == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("Invalid movement direction! Valid movement directions: %s.".formatted(EnumUtils.joinEnumValues(MoveDirection.class)))
                    .execute();

            return 0;
        }
        movementDirectionString = movementDirectionString.toLowerCase();

        KeyBinding key = moveDirection.function.apply(source.getClient().options);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Now moving %s for %d seconds...".formatted(movementDirectionString, amount))
                .execute();

        new KeyBindingBuilder().source(source)
                .keys(key)
                .printLogs(false)
                .execute();
        new KeyBindingBuilder().source(source)
                .keys(key)
                .pressed(false)
                .extraTickDelay(amount * 20)
                .execute();

        return 1;
    }

    public static int executeDistance(FabricClientCommandSource source, String movementDirectionString, int amount) {
        ClientPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        MoveDirection moveDirection = EnumUtils.getValueSafely(MoveDirection.class, movementDirectionString);
        if (moveDirection == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("Invalid movement direction! Valid movement directions: %s.".formatted(EnumUtils.joinEnumValues(MoveDirection.class)))
                    .execute();

            return 0;
        }
        movementDirectionString = movementDirectionString.toLowerCase();

        KeyBinding key = moveDirection.function.apply(source.getClient().options);

        new KeyBindingBuilder().source(source)
                .keys(key)
                .printLogs(false)
                .execute();

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Now moving %s for %d seconds...".formatted(movementDirectionString, amount))
                .execute();

        isRunning = true;

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
