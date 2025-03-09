package io.github.brainage04.twitchplaysminecraft.command.use;

import io.github.brainage04.twitchplaysminecraft.command.key.ToggleKeyCommands;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.RunnableScheduler;
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

    private static float prevYaw = 0;
    private static float prevPitch = 0;

    public static int stop(FabricClientCommandSource source) {
        // this stops the player from falling off accidentally
        GameOptions options = source.getClient().options;

        ToggleKeyCommands.removeKeys(source, new KeyBinding[]{
                options.useKey,
                options.backKey,
                options.rightKey
        }, false);
        RunnableScheduler.scheduleTask(() -> ToggleKeyCommands.removeKey(source, options.sneakKey, false));

        source.getPlayer().setYaw(prevYaw);
        source.getPlayer().setPitch(prevPitch);

        isRunning = false;

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
        CardinalDirection cardinalDirection = EnumUtils.getValueSafely(CardinalDirection.class, cardinalDirectionString.toLowerCase());
        if (cardinalDirection == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Invalid direction! Valid directions: %s.".formatted(EnumUtils.joinEnumValues(CardinalDirection.class)))
                    .execute();

            return 0;
        }

        if (!(source.getPlayer().getMainHandStack().getItem() instanceof BlockItem)) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You are not holding a block!")
                    .execute();

            return 0;
        }

        prevYaw = source.getPlayer().getYaw();
        prevPitch = source.getPlayer().getPitch();

        // look and move in opposite direction of cardinal direction
        int offset = cardinalDirection.isDiagonal() ? 0 : 45;
        source.getPlayer().setYaw(cardinalDirection.yaw - 180 + offset);

        // look almost straight down
        source.getPlayer().setPitch(77.5F);

        GameOptions options = source.getClient().options;

        List<KeyBinding> keys = new ArrayList<>(List.of(options.useKey, options.backKey));
        if (!cardinalDirection.isDiagonal()) keys.add(options.rightKey);

        ToggleKeyCommands.addKey(source, options.sneakKey, false);
        RunnableScheduler.scheduleTask(() -> ToggleKeyCommands.addKeys(source, keys.toArray(KeyBinding[]::new), false));

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Bridging %s for %d blocks...".formatted(cardinalDirection.name(), count))
                .execute();

        isRunning = true;
        blocksPlaced = 0;
        blocksPlacedLimit = count;



        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
