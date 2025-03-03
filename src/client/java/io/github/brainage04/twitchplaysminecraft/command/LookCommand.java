package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.LookDirection;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class LookCommand {
    private static LookDirection getLookDirectionSafely(String value) {
        try {
            return LookDirection.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int execute(FabricClientCommandSource source, String lookDirectionString, int degrees) {
        LookDirection lookDirection = getLookDirectionSafely(lookDirectionString.toUpperCase());
        if (lookDirection == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Invalid direction! Valid directions: %s.".formatted(EnumUtils.joinEnumValues(LookDirection.class)))
                    .execute();
            return 0;
        }

        if (source.getClient().player == null) return 0;
        lookDirection.consumer.accept(source.getClient().player, degrees);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Now looking %s %d degrees.".formatted(lookDirectionString.toLowerCase(), degrees))
                .execute();

        return 1;
    }
}
