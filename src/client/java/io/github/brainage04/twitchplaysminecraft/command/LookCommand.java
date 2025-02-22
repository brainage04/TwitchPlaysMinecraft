package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Arrays;

public class LookCommand {
    public enum LookDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

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
                    .text("Invalid direction! Valid directions: %s.".formatted(String.join(", ", Arrays.stream(LookDirection.values()).map(Enum::name).toArray(String[]::new))))
                    .execute();
            return 0;
        }

        ClientPlayerEntity player = source.getClient().player;
        if (player == null) return 0;

        switch (lookDirection) {
            case UP:
                player.setPitch(player.getPitch() - degrees);
                break;
            case DOWN:
                player.setPitch(player.getPitch() + degrees);
                break;
            case LEFT:
                player.setYaw(player.getYaw() - degrees);
                break;
            case RIGHT:
                player.setYaw(player.getYaw() + degrees);
                break;
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Now looking %s %d degrees.".formatted(lookDirectionString.toLowerCase(), degrees))
                .execute();

        return 1;
    }
}
