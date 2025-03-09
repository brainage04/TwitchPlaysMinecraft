package io.github.brainage04.twitchplaysminecraft.command.look;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.enums.CardinalDirection;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

// use this one instead of the other one when copy pasting:
//@SuppressWarnings("SameReturnValue")
@SuppressWarnings({"unused", "SameReturnValue"})
public class TurnAroundCommand {
    private static boolean inBetween(float min, float yaw, float max) {
        return min <= yaw && yaw <= max;
    }

    public static CardinalDirection getDirection(float yaw) {
        if (inBetween(CardinalDirection.NORTH.yaw - 22.5F, yaw, CardinalDirection.NORTH.yaw + 22.5F)) return CardinalDirection.NORTH;
        if (inBetween(CardinalDirection.NORTHEAST.yaw - 22.5F, yaw, CardinalDirection.NORTHEAST.yaw + 22.5F)) return CardinalDirection.NORTHEAST;
        if (inBetween(CardinalDirection.EAST.yaw - 22.5F, yaw, CardinalDirection.EAST.yaw + 22.5F)) return CardinalDirection.EAST;
        if (inBetween(CardinalDirection.SOUTHEAST.yaw - 22.5F, yaw, CardinalDirection.SOUTHEAST.yaw + 22.5F)) return CardinalDirection.SOUTHEAST;
        if (inBetween(CardinalDirection.SOUTH.yaw - 22.5F, yaw, CardinalDirection.SOUTH.yaw + 22.5F)) return CardinalDirection.SOUTH;
        if (inBetween(CardinalDirection.SOUTHWEST.yaw - 22.5F, yaw, CardinalDirection.SOUTHWEST.yaw + 22.5F)) return CardinalDirection.SOUTHWEST;
        if (inBetween(CardinalDirection.WEST.yaw - 22.5F, yaw, CardinalDirection.WEST.yaw + 22.5F)) return CardinalDirection.WEST;
        if (inBetween(CardinalDirection.NORTHWEST.yaw - 22.5F, yaw, CardinalDirection.NORTHWEST.yaw + 22.5F)) return CardinalDirection.NORTHWEST;

        TwitchPlaysMinecraft.LOGGER.error("getDirection not resolved - this shouldn't happen");
        return CardinalDirection.NORTH;
    }

    public static int execute(FabricClientCommandSource source) {
        CardinalDirection before = getDirection(source.getPlayer().getYaw());
        CardinalDirection after = getDirection(source.getPlayer().getYaw() + 180);

        source.getPlayer().setYaw(after.yaw);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Turned around from %s to %s.".formatted(before.getName(), after.getName()))
                .execute();

        return 1;
    }
}
