package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.TreeDetector;
import io.github.brainage04.twitchplaysminecraft.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.MessageType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.TreeSet;

import static io.github.brainage04.twitchplaysminecraft.util.BlockPosUtils.compareBlockPos;

public class MineTreeCommand {
    // todo: use this code for all mine commands instead of just mine tree
    private static final int radius = 8;

    private static boolean isMining = false;
    private static TreeSet<BlockPos> tree = new TreeSet<>(Comparator.comparingInt(BlockPos::getY));

    public static void initialize() {
        ClientPlayerBlockBreakEvents.AFTER.register(((clientWorld, clientPlayerEntity, blockPos, blockState) -> {
            if (!isMining) return;

            if (tree.removeIf(pos -> compareBlockPos(pos, blockPos))) {
                TwitchPlaysMinecraft.LOGGER.info("Block removed");
            } else {
                TwitchPlaysMinecraft.LOGGER.error("No block removed - this shouldn't happen!");
            }

            if (tree.isEmpty()) {
                new FeedbackBuilder().source(clientPlayerEntity.client)
                        .messageType(MessageType.SUCCESS)
                        .text("Finished mining tree.")
                        .execute();
                isMining = false;
                tree.clear();
            }
        }));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isMining) return;
            if (client.player == null) return;

            client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, tree.first().toCenterPos());
        });
    }

    public static int execute(FabricClientCommandSource source) {
        BlockPos start = source.getPlayer().getBlockPos();
        for (int x = start.getX() - radius; x < start.getX() + radius; x++) {
            for (int y = start.getY() - radius; y < start.getY() + radius; y++) {
                for (int z = start.getZ() - radius; z < start.getZ() + radius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = source.getWorld().getBlockState(pos);
                    if (!state.isIn(TreeDetector.LOGS_TAG)) continue;

                    tree = TreeDetector.getLogsInTree(tree, source.getWorld(), pos);

                    if (!tree.isEmpty()) break;
                }
            }
        }

        if (tree.isEmpty()) {
            new FeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No tree could be found within %d blocks! Please try again.".formatted(radius))
                    .execute();
            return 0;
        }

        isMining = true;
        new KeyBindingBuilder().source(source).keys(source.getClient().options.attackKey).execute();

        new FeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Player is now mining a tree...")
                .execute();

        return 1;
    }
}
