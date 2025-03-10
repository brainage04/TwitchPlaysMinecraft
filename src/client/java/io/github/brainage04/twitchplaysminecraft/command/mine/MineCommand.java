package io.github.brainage04.twitchplaysminecraft.command.mine;

import io.github.brainage04.twitchplaysminecraft.command.key.ToggleKeyCommands;
import io.github.brainage04.twitchplaysminecraft.command.look.FaceBlockCommand;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class MineCommand {
    private static boolean isRunning = false;
    private static Block block = null;
    private static Vec3d nextBlockPos = null;
    private static int blocksBroken = 0;
    private static int blocksBrokenLimit = Integer.MAX_VALUE;
    private static int ticksSinceLastBlockBreak = 0;
    private static final int secondsSinceLastBlockBreakLimit = 15;

    public static void initialize() {
        ClientPlayerBlockBreakEvents.AFTER.register(((clientWorld, clientPlayerEntity, blockPos, blockState) -> {
            if (!isRunning) return;
            if (block == null) return;

            blocksBroken++;
            ticksSinceLastBlockBreak = 0;

            if (blocksBroken >= blocksBrokenLimit) {
                new ClientFeedbackBuilder().source(clientPlayerEntity)
                        .messageType(MessageType.SUCCESS)
                        .text("%d blocks have been mined. Stopping...".formatted(blocksBrokenLimit))
                        .execute();

                stop(SourceUtils.getSource(clientPlayerEntity));

                return;
            }

            nextBlockPos = FaceBlockCommand.locateVisibleReachableBlock(SourceUtils.getSource(clientPlayerEntity), block);
            if (nextBlockPos == null) {
                new ClientFeedbackBuilder().source(clientPlayerEntity)
                        .messageType(MessageType.ERROR)
                        .text(Text.literal("No more visible/reachable ")
                                .append(block.getName())
                                .append("! Stopping..."))
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

                return;
            }

            if (nextBlockPos != null) {
                client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, nextBlockPos);
            }
        });
    }


    public static void stop(FabricClientCommandSource source) {
        ToggleKeyCommands.removeKey(source, source.getClient().options.attackKey, false);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Mining cancelled.")
                .execute();

        isRunning = false;

    }

    private static final List<String> INVALID_BLOCKS = List.of(
            "air",
            "water",
            "lava"
    );

    public static int execute(FabricClientCommandSource source, String blockName, int count) {
        if (isRunning) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You are already mining something!")
                    .execute();

            return 0;
        }

        if (INVALID_BLOCKS.contains(blockName.toLowerCase())) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You cannot mine \"%s\"! Please try again.".formatted(blockName))
                    .execute();
            return 0;
        }

        block = Registries.BLOCK.get(Identifier.of(blockName));
        if (block == Blocks.AIR) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Unknown block \"%s\"! Please try again.".formatted(blockName))
                    .execute();

            return 0;
        }

        nextBlockPos = FaceBlockCommand.locateVisibleReachableBlock(SourceUtils.getSource(source.getPlayer()), block);
        if (nextBlockPos == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text(Text.literal("No visible/reachable ")
                            .append(block.getName())
                            .append("!"))
                    .execute();

            return 0;
        }

        ToggleKeyCommands.addKey(source, source.getClient().options.attackKey, false);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Player is now mining %d ".formatted(count))
                        .append(block.getName())
                        .append("..."))
                .execute();

        isRunning = true;
        blocksBroken = 0;
        blocksBrokenLimit = count;
        ticksSinceLastBlockBreak = 0;

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
