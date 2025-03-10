package io.github.brainage04.twitchplaysminecraft.command.use;

import io.github.brainage04.twitchplaysminecraft.command.key.ToggleKeyCommands;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.BlockItem;

public class JumpPlaceCommand {
    private static boolean isRunning = false;
    private static int blocksPlaced = 0;
    private static int blocksPlacedLimit = Integer.MAX_VALUE;

    public static void stop(FabricClientCommandSource source) {
        GameOptions options = source.getClient().options;
        ToggleKeyCommands.removeKeys(source, new KeyBinding[]{
                options.jumpKey,
                options.useKey
        }, false);

        isRunning = false;

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

        // hold jump and right click
        GameOptions options = source.getClient().options;
        ToggleKeyCommands.addKeys(source, new KeyBinding[]{
                options.jumpKey,
                options.useKey
        }, false);

        isRunning = true;
        blocksPlaced = 0;
        blocksPlacedLimit = count;

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Jumping and placing %d blocks...".formatted(count))
                .execute();

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
