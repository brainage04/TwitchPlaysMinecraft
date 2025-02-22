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
    private static final int radius = 8;

    private static boolean isMining = false;
    private static TreeSet<BlockPos> blocks = new TreeSet<>();

    public static void initialize() {
        ClientPlayerBlockBreakEvents.AFTER.register(((clientWorld, clientPlayerEntity, blockPos, blockState) -> {
            if (!isMining) return;

            if (blocks.isEmpty()) {
                isMining = false;
                ReleaseAllKeysCommand.execute(SourceUtils.getSourceFromClient(clientPlayerEntity.client));
                new ClientFeedbackBuilder().source(clientPlayerEntity.client)
                        .messageType(MessageType.SUCCESS)
                        .text("Finished mining blocks.")
                        .execute();
            }
        }));
        
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isMining) return;
            if (client.player == null) return;
            if (client.world == null) return;
            if (blocks.isEmpty()) return;

            BlockPos desiredBlockPos = blocks.first();

            Direction face = BlockUtils.canSeeBlockFace(client.player, desiredBlockPos);
            Vec3d target = face != null ? BlockUtils.getFacePos(desiredBlockPos, face) : desiredBlockPos.toCenterPos();

            client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target);
        });
    }

    // todo: wtf do i do with this again?
    public static TreeSet<BlockPos> getLogsInTree(World world, BlockPos start, int limit) {
        Block desiredBlock = world.getBlockState(start).getBlock();
        TreeSet<BlockPos> blocks = new TreeSet<>(Comparator.comparingInt(start::getManhattanDistance));
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

        return blocks;
    }

    private static TreeSet<BlockPos> getBlocks(ClientPlayerEntity player, Block block, int limit) {
        BlockPos center = player.getBlockPos();
        TreeSet<BlockPos> blocks = new TreeSet<>(Comparator.comparingInt(center::getManhattanDistance));

        for (int x = center.getX() - radius; x < center.getX() + radius; x++) {
            for (int y = center.getY() - radius; y < center.getY() + radius; y++) {
                for (int z = center.getZ() - radius; z < center.getZ() + radius; z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);

                    if (player.clientWorld.getBlockState(blockPos).isOf(block)) {
                        blocks.add(blockPos);
                    }
                }
            }
        }

        while (blocks.size() > limit) {
            blocks.removeLast();
        }

        return blocks;
    }

    private static final String[] INVALID_BLOCKS = new String[]{
            "air",
            "water",
            "lava"
    };

    public static int execute(FabricClientCommandSource source, String blockName, int count) {
        if (Arrays.stream(INVALID_BLOCKS).anyMatch(blockName.toLowerCase()::matches)) {
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

        blocks = getBlocks(source.getPlayer(), block, count);
        if (blocks.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No blocks found!")
                    .execute();
            return 0;
        }

        GameOptions options = source.getClient().options;
        new KeyBindingBuilder().source(source)
                .printLogs(false)
                .keys(options.attackKey, options.sneakKey, options.forwardKey)
                .execute();

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Player is now mining %d ".formatted(count))
                        .append(block.getName())
                        .append("s..."))
                .execute();

        isMining = true;

        return 1;
    }
}
