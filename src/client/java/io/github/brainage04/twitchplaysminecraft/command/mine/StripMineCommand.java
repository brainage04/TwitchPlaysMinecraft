package io.github.brainage04.twitchplaysminecraft.command.mine;

import io.github.brainage04.twitchplaysminecraft.command.key.ToggleKeyCommands;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.RunnableScheduler;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.client.option.KeyBinding;

@SuppressWarnings("SameReturnValue")
public class StripMineCommand {
    private static boolean isRunning = false;
    private static int blocksBroken = 0;
    private static int blocksBrokenLimit = Integer.MAX_VALUE;
    private static int ticksSinceLastBlockBreak = 0;
    private static final int secondsSinceLastBlockBreakLimit = 15;

    public static void stop(FabricClientCommandSource source) {
        ToggleKeyCommands.removeKeys(source, new KeyBinding[]{
                source.getClient().options.attackKey,
                source.getClient().options.forwardKey
        }, false);
        RunnableScheduler.scheduleTask(() -> ToggleKeyCommands.removeKey(source, source.getClient().options.sneakKey, false));

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Mining cancelled.")
                .execute();

        isRunning = false;

    }

    public static void initialize() {
        ClientPlayerBlockBreakEvents.AFTER.register(((clientWorld, clientPlayerEntity, blockPos, blockState) -> {
            if (!isRunning) return;

            blocksBroken++;
            ticksSinceLastBlockBreak = 0;

            if (blocksBroken >= blocksBrokenLimit) {
                new ClientFeedbackBuilder().source(clientPlayerEntity)
                        .messageType(MessageType.SUCCESS)
                        .text("%d blocks have been mined. Stopping...".formatted(blocksBrokenLimit))
                        .execute();

                stop(SourceUtils.getSource(clientPlayerEntity));
            }
        }));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (client.player == null) return;

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
        ToggleKeyCommands.addKeys(source, new KeyBinding[]{
                source.getClient().options.sneakKey,
                source.getClient().options.forwardKey,
                source.getClient().options.attackKey
        }, false);

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

        ToggleKeyCommands.addKey(source, source.getClient().options.sneakKey, false);
        RunnableScheduler.scheduleTask(() -> ToggleKeyCommands.addKeys(source, new KeyBinding[]{
                source.getClient().options.attackKey,
                source.getClient().options.forwardKey
        }, false));

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Player is now strip mining %d blocks...".formatted(blocksToBreak))
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
