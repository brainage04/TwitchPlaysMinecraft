package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.item.BlockItem;

public class JumpPlaceCommand {
    private static boolean isRunning = false;
    private static int blocksPlaced = 0;
    private static int blocksPlacedLimit = Integer.MAX_VALUE;

    public static void incrementBlocksPlaced() {
        if (!isRunning) return;

        blocksPlaced++;
        if (blocksPlaced < blocksPlacedLimit) {
            new ClientFeedbackBuilder().messageType(MessageType.INFO)
                    .text("Placed %d/%d...".formatted(blocksPlaced, blocksPlacedLimit))
                    .execute();
        } else {
            new ClientFeedbackBuilder().messageType(MessageType.SUCCESS)
                    .text("Placed %d/%d.".formatted(blocksPlaced, blocksPlacedLimit))
                    .execute();

            GameOptions options = MinecraftClient.getInstance().options;
            new KeyBindingBuilder().keys(options.useKey, options.jumpKey)
                    .pressed(false)
                    .execute();

            isRunning = false;
            blocksPlaced = 0;
            blocksPlacedLimit = Integer.MAX_VALUE;
        }
    }

    public static int execute(FabricClientCommandSource source, int count) {
        if (!(source.getPlayer().getMainHandStack().getItem() instanceof BlockItem)) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You are not holding a block!")
                    .execute();
            return 0;
        }

        // look straight down
        source.getPlayer().setPitch(90);

        GameOptions options = source.getClient().options;
        new KeyBindingBuilder().source(source)
                .keys(options.useKey, options.jumpKey)
                .execute();

        isRunning = true;
        blocksPlacedLimit = count;

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Jumping and placing %d blocks...".formatted(count))
                .execute();

        return 1;
    }
}
