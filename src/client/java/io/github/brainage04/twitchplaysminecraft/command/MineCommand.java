package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.BlockUtils;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class MineCommand {
    private static boolean isMining = false;
    private static final TreeSet<BlockPos> blocks = new TreeSet<>();
    private static int ticksSinceLastBlockBreak = 0;
    private static final int secondsSinceLastBlockBreakLimit = 15;

    private static void stop() {
        isMining = false;
        ticksSinceLastBlockBreak = 0;
    }

    public static void initialize() {
        ClientPlayerBlockBreakEvents.AFTER.register(((clientWorld, clientPlayerEntity, blockPos, blockState) -> {
            if (!isMining) return;

            ticksSinceLastBlockBreak = 0;

            if (blocks.isEmpty()) {
                new ClientFeedbackBuilder().source(clientPlayerEntity.client)
                        .messageType(MessageType.SUCCESS)
                        .text("Finished mining blocks.")
                        .execute();

                ReleaseAllKeysCommand.execute(SourceUtils.getSourceFromClient(clientPlayerEntity.client));

                stop();
            }
        }));
        
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isMining) return;
            if (client.player == null) return;

            ticksSinceLastBlockBreak++;
            if (ticksSinceLastBlockBreak >= secondsSinceLastBlockBreakLimit * 20) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.ERROR)
                        .text("No blocks mined for %d seconds! Stopping...".formatted(secondsSinceLastBlockBreakLimit))
                        .execute();

                stop();
            }

            if (blocks.isEmpty()) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.ERROR)
                        .text("Still mining with no blocks left to mine - this shouldn't happen!")
                        .execute();

                stop();

                return;
            }

            BlockPos desiredBlockPos = blocks.first();

            Direction face = BlockUtils.canSeeBlockFace(client.player, desiredBlockPos);
            Vec3d target = face != null ? BlockUtils.getFacePos(desiredBlockPos, face) : desiredBlockPos.toCenterPos();

            client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target);
        });
    }

    public static void updateBlocks(ClientPlayerEntity player, Block desiredBlock, int limit) {
        World world = player.getWorld();
        BlockPos start = player.getBlockPos();

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(start);

        while (!queue.isEmpty() && blocks.size() < limit) {
            BlockPos pos = queue.poll();

            Block currentBlock = world.getBlockState(pos).getBlock();
            if (currentBlock == desiredBlock) blocks.add(pos);

            // Check all 26 neighboring positions in a 3×3×3 cube
            for (int dy = -1; dy <= 1; dy++) { // y is DELIBERATELY out of order (specifically first) to help with sorting by Y level
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        // Skip the current block
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos neighbor = pos.add(dx, dy, dz);

                        // Skip already visited blocks
                        if (visited.contains(neighbor)) continue;

                        // Add to queue for further exploration
                        queue.add(neighbor);
                    }
                }
            }

            visited.add(pos);
        }
    }

    private static final List<String> INVALID_BLOCKS = List.of(
            "air",
            "water",
            "lava"
    );

    public static int execute(FabricClientCommandSource source, String blockName, int count) {
        if (INVALID_BLOCKS.contains(blockName.toLowerCase())) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You cannot mine \"%s\"! Please try again.".formatted(blockName))
                    .execute();
            return 0;
        }

        Block block = Registries.BLOCK.get(Identifier.of(blockName));
        if (block == Blocks.AIR) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Unknown block \"%s\"! Please try again.".formatted(blockName))
                    .execute();
            return 0;
        }

        // check for blocks within reach (radius of 3)
        // when block is found, perform flood fill algorithm to get all blocks in the same vein
        // repeat process and increment radius up to 20

        int radius = 3;
        updateBlocks(source.getPlayer(), block, count);
        if (blocks.size() < count) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.literal("Less than %d ".formatted(count))
                            .append(block.getName())
                            .append(" blocks found within 3 blocks. Checking up to 20 blocks..."))
                    .execute();

            radius++;
            while (radius <= 20) {
                updateBlocks(source.getPlayer(), block, count);
                if (blocks.size() >= count) break;

                radius++;
            }
        }

        if (blocks.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text(Text.literal("No ")
                            .append(block.getName())
                            .append(" blocks found!"))
                    .execute();

            return 0;
        } else if (blocks.size() < count) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.literal("Only %d/%d ".formatted(blocks.size(), count))
                            .append(block.getName())
                            .append(" blocks found."))
                    .execute();
        }

        GameOptions options = source.getClient().options;
        new KeyBindingBuilder().source(source)
                .printLogs(false)
                .keys(options.attackKey, options.sneakKey, options.forwardKey)
                .execute();

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Player is now mining %d ".formatted(blocks.size()))
                        .append(block.getName())
                        .append(" blocks..."))
                .execute();

        isMining = true;


        return 1;
    }
}
