package io.github.brainage04.twitchplaysminecraft.command.look;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.CardinalDirection;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class FaceCommand {
    public static int execute(FabricClientCommandSource source, String cardinalDirectionString) {
        cardinalDirectionString = cardinalDirectionString.toLowerCase();
        CardinalDirection cardinalDirection = EnumUtils.getValueSafely(CardinalDirection.class, cardinalDirectionString);
        if (cardinalDirection == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Invalid direction! Valid directions: %s.".formatted(EnumUtils.joinEnumValues(CardinalDirection.class)))
                    .execute();

            return 0;
        }

        if (source.getClient().player == null) return 0;
        source.getClient().player.setYaw(cardinalDirection.yaw);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Now facing %s.".formatted(cardinalDirectionString))
                .execute();

        return 1;
    }
}
