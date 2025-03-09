package io.github.brainage04.twitchplaysminecraft.command.move;

import io.github.brainage04.twitchplaysminecraft.command.key.ReleaseAllKeysCommand;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.RunnableScheduler;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("SameReturnValue")
public class MoveDirectionCommands {
    private static boolean isRunning = false;
    private static BlockPos startPos = null;
    private static int distance = 0;

    public static int stop(FabricClientCommandSource source) {
        ReleaseAllKeysCommand.releaseAllKeys(source);

        isRunning = false;

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Finished movement.")
                .execute();

        return 0;
    }

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (startPos == null) return;
            if (distance <= 0) return;
            if (client.player == null) return;
            if (startPos.getSquaredDistance(client.player.getBlockPos()) < distance * distance) return;

            stop(SourceUtils.getSource(client.player));
        });
    }

    public static int executeTime(FabricClientCommandSource source, KeyBinding key, int ticks) {
        key.setPressed(true);
        RunnableScheduler.scheduleTask(() -> key.setPressed(false), ticks);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Now holding ")
                        .append(Text.translatable(key.getTranslationKey()))
                        .append(" for %d ticks...".formatted(ticks)))
                .execute();

        return 1;
    }

    public static int executeDistance(FabricClientCommandSource source, KeyBinding key, int blocks) {
        key.setPressed(true);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Now holding ")
                        .append(Text.translatable(key.getTranslationKey()))
                        .append(" for %d blocks...".formatted(blocks)))
                .execute();

        isRunning = true;
        startPos = source.getPlayer().getBlockPos();
        distance = blocks;

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
