package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.enums.MovementDirection;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.KeyBinding;

public class MoveCommand {


    public static int executeTime(FabricClientCommandSource source, String movementDirectionString, int amount) {
        MovementDirection movementDirection = EnumUtils.getValueSafely(MovementDirection.class, movementDirectionString);
        if (movementDirection == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("Invalid movement direction! Valid movement directions: %s.".formatted(EnumUtils.joinEnumValues(MovementDirection.class)))
                    .execute();

            return 0;
        }
        movementDirectionString = movementDirectionString.toLowerCase();

        KeyBinding key = movementDirection.function.apply(source.getClient().options);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Now moving %s for %d seconds...".formatted(movementDirectionString, amount));

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
        MovementDirection movementDirection = EnumUtils.getValueSafely(MovementDirection.class, movementDirectionString);
        if (movementDirection == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("Invalid movement direction! Valid movement directions: %s.".formatted(EnumUtils.joinEnumValues(MovementDirection.class)))
                    .execute();

            return 0;
        }
        movementDirectionString = movementDirectionString.toLowerCase();

        KeyBinding key = movementDirection.function.apply(source.getClient().options);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Now moving %s for %d blocks...".formatted(movementDirectionString, amount));

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
}
