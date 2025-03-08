package io.github.brainage04.twitchplaysminecraft.command.mine;

import io.github.brainage04.twitchplaysminecraft.command.key.ReleaseAllKeysCommand;
import io.github.brainage04.twitchplaysminecraft.command.key.ToggleKeyCommands;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

@SuppressWarnings("SameReturnValue")
public class StripMineCommand {
    private static boolean isRunning = false;
    private static int blocksBroken = 0;
    private static int blocksBrokenLimit = Integer.MAX_VALUE;
    private static int ticksSinceLastBlockBreak = 0;
    private static final int secondsSinceLastBlockBreakLimit = 15;

    public static int stop(FabricClientCommandSource source) {
        isRunning = false;

        ReleaseAllKeysCommand.execute(source);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Mining cancelled.")
                .execute();

        return 1;
    }

    public static void initialize() {
        ClientPlayerBlockBreakEvents.AFTER.register(((clientWorld, clientPlayerEntity, blockPos, blockState) -> {
            if (!isRunning) return;

            blocksBroken++;
            ticksSinceLastBlockBreak = 0;
        }));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (client.player == null) return;

            if (blocksBroken >= blocksBrokenLimit) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.SUCCESS)
                        .text("%d blocks have been mined. Stopping...".formatted(blocksBrokenLimit))
                        .execute();

                stop(SourceUtils.getSource(client.player));
            }

            ticksSinceLastBlockBreak++;
            if (ticksSinceLastBlockBreak >= secondsSinceLastBlockBreakLimit * 20) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.ERROR)
                        .text("No blocks mined for %d seconds! Stopping...".formatted(secondsSinceLastBlockBreakLimit))
                        .execute();

                stop(SourceUtils.getSource(client.player));
            }
        });
    }

    @SuppressWarnings("SameReturnValue")
    public static int execute(FabricClientCommandSource source) {
        GameOptions options = source.getClient().options;
        new KeyBindingBuilder().source(source)
                .keys(options.sneakKey, options.forwardKey, options.attackKey)
                .execute();

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Player is now strip mining...")
                .execute();

        return 1;
    }

    public static int execute(FabricClientCommandSource source, int blocksToBreak) {
        if (isRunning) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You are already mining something!")
                    .execute();

            return 0;
        }

        // 25 pitch allows breaking of both top
        // and bottom block without having to move camera
        source.getPlayer().setPitch(25);

        ToggleKeyCommands.toggleKeys(source, new KeyBinding[]{
                source.getClient().options.attackKey,
                source.getClient().options.sneakKey,
                source.getClient().options.forwardKey
        }, false);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Player is now strip mining %d blocks...".formatted(blocksBrokenLimit))
                .execute();

        isRunning = true;
        blocksBroken = 0;
        blocksBrokenLimit = blocksToBreak;
        ticksSinceLastBlockBreak = 0;

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
