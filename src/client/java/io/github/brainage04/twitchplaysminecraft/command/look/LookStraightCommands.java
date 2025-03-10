package io.github.brainage04.twitchplaysminecraft.command.look;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.enums.CardinalDirection;
import io.github.brainage04.twitchplaysminecraft.util.enums.LookStraightDirection;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class LookStraightCommands {
    public static int execute(FabricClientCommandSource source, LookStraightDirection lookStraightDirection) {
        float newYaw = getDirection(source.getPlayer().getYaw() + lookStraightDirection.yaw).yaw;
        float newPitch = lookStraightDirection.pitch;

        source.getPlayer().rotate(newYaw, newPitch);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Now looking straight %s.".formatted(lookStraightDirection.getName()))
                .execute();

        return 1;
    }

    public static CardinalDirection getDirection(float yaw) {
        // (-180, 180) -> (0, 360)
        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 337.5 || yaw < 22.5) {
            return CardinalDirection.SOUTH;
        } else if (yaw >= 22.5 && yaw < 67.5) {
            return CardinalDirection.SOUTHWEST;
        } else if (yaw >= 67.5 && yaw < 112.5) {
            return CardinalDirection.WEST;
        } else if (yaw >= 112.5 && yaw < 157.5) {
            return CardinalDirection.NORTHWEST;
        } else if (yaw >= 157.5 && yaw < 202.5) {
            return CardinalDirection.NORTH;
        } else if (yaw >= 202.5 && yaw < 247.5) {
            return CardinalDirection.NORTHEAST;
        } else if (yaw >= 247.5 && yaw < 292.5) {
            return CardinalDirection.EAST;
        } else if (yaw >= 292.5 && yaw < 337.5) {
            return CardinalDirection.SOUTHEAST;
        }

        TwitchPlaysMinecraft.LOGGER.info("getDirection fallback - this shouldn't happen");
        return CardinalDirection.NORTH;
    }
}
