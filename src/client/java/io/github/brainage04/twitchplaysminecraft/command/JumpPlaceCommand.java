package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.MessageType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.GameOptions;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class JumpPlaceCommand {
    private static boolean isRunning = false;
    private static int blocksPlaced = 0;
    private static int blocksPlacedLimit = Integer.MAX_VALUE;

    public static void initialize() {
        UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
            if (!world.isClient) return ActionResult.PASS;
            if (!isRunning) return ActionResult.PASS;

            // todo: get this to work
            BlockPos pos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
            BlockState state = world.getBlockState(pos);
            if (state.isOf(Blocks.AIR)) return ActionResult.PASS;

            blocksPlaced++;
            if (blocksPlaced < blocksPlacedLimit) {
                new FeedbackBuilder().messageType(MessageType.INFO)
                        .text("Placed %d/%d...".formatted(blocksPlaced, blocksPlacedLimit))
                        .execute();
            } else {
                new FeedbackBuilder().messageType(MessageType.SUCCESS)
                        .text("Placed %d/%d.".formatted(blocksPlaced, blocksPlacedLimit))
                        .execute();
                ReleaseAllKeysCommand.execute(SourceUtils.getSourceFromClient());
                isRunning = false;
                blocksPlaced = 0;
                blocksPlacedLimit = Integer.MAX_VALUE;
            }

            return ActionResult.PASS;
        }));
    }

    public static int execute(FabricClientCommandSource source, int count) {
        if (!(source.getPlayer().getMainHandStack().getItem() instanceof BlockItem)) {
            new FeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You are not holding a block!")
                    .execute();
            return 0;
        }

        // todo: execute command that moves player to the centre of a block

        // look straight down
        source.getPlayer().setPitch(90);

        // hold jump and right click
        GameOptions options = source.getClient().options;
        new KeyBindingBuilder().source(source).keys(options.useKey, options.jumpKey).execute();

        isRunning = true;
        blocksPlacedLimit = count;
        new FeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Jumping and placing %d blocks...".formatted(count))
                .execute();

        return 1;
    }
}
