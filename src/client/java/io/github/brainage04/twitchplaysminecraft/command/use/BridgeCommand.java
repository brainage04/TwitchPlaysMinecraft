package io.github.brainage04.twitchplaysminecraft.command.use;

import io.github.brainage04.twitchplaysminecraft.command.ReleaseAllKeysCommand;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.CardinalDirection;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.BlockItem;

import java.util.ArrayList;
import java.util.List;

public class BridgeCommand {
    private static boolean isRunning = false;
    private static int blocksPlaced = 0;
    private static int blocksPlacedLimit = Integer.MAX_VALUE;

    public static int stop(FabricClientCommandSource source) {
        ReleaseAllKeysCommand.execute(source);

        isRunning = false;
        blocksPlaced = 0;
        blocksPlacedLimit = Integer.MAX_VALUE;

        return 1;
    }

    public static void incrementBlocksPlaced(MinecraftClient client) {
        if (!isRunning) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        blocksPlaced++;
        if (blocksPlaced < blocksPlacedLimit) {
            new ClientFeedbackBuilder().messageType(MessageType.INFO)
                    .text("Placed %d/%d...".formatted(blocksPlaced, blocksPlacedLimit))
                    .execute();
        } else {
            new ClientFeedbackBuilder().messageType(MessageType.SUCCESS)
                    .text("Placed %d/%d.".formatted(blocksPlaced, blocksPlacedLimit))
                    .execute();

            stop(SourceUtils.getSource(player));
        }
    }

    public static int execute(FabricClientCommandSource source, String cardinalDirectionString, int count) {
        ClientPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        CardinalDirection cardinalDirection = EnumUtils.getValueSafely(CardinalDirection.class, cardinalDirectionString.toLowerCase());
        if (cardinalDirection == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Invalid direction! Valid directions: %s.".formatted(EnumUtils.joinEnumValues(CardinalDirection.class)))
                    .execute();

            return 0;
        }

        if (!(player.getMainHandStack().getItem() instanceof BlockItem)) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You are not holding a block!")
                    .execute();

            return 0;
        }

        // look and move in opposite direction of cardinal direction
        int offset = cardinalDirection.isDiagonal() ? 0 : 45;
        player.setYaw(cardinalDirection.getYaw() - 180 + offset);
        // look almost straight down
        player.setPitch(77.5F);

        GameOptions options = source.getClient().options;
        List<KeyBinding> keys = new ArrayList<>(List.of(options.useKey, options.sneakKey, options.backKey));
        if (!cardinalDirection.isDiagonal()) keys.add(options.rightKey);

        new KeyBindingBuilder().source(source)
                .keys(keys.toArray(KeyBinding[]::new))
                .execute();

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Bridging %s for %d blocks...".formatted(cardinalDirection.name(), count))
                .execute();

        isRunning = true;
        blocksPlacedLimit = count;

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
