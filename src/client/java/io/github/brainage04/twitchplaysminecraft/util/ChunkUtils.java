package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

public class ChunkUtils {
    public static <T extends BlockEntity> List<T> getBlockEntities(BlockEntityType<T> type, ClientWorld world) {
        List<T> blockEntities = new ArrayList<>();

        for (int i = 0; i < world.getChunkManager().getLoadedChunkCount(); i++) {
            Chunk chunk = world.getChunkManager().chunks.chunks.get(i);
            if (chunk == null) continue;

            for (BlockEntity blockEntity : chunk.blockEntities.values()) {
                if (blockEntity.getType() == type) {
                    //noinspection unchecked
                    blockEntities.add((T) blockEntity);
                }
            }
        }

        return blockEntities;
    }

    public static List<BlockPos> getBlocks(Block block, ClientWorld world) {
        List<BlockPos> blocks = new ArrayList<>();

        for (int i = 0; i < world.getChunkManager().getLoadedChunkCount(); i++) {
            Chunk chunk = world.getChunkManager().chunks.chunks.get(i);
            if (chunk == null) continue;

            for (int x = 0; x < 15; x++) {
                for (int y = world.getBottomY(); y < world.getTopYInclusive(); y++) {
                    for (int z = 0; z < 15; z++) {
                        BlockPos pos = chunk.getPos().getStartPos().add(x, y, z);

                        if (world.getBlockState(pos).getBlock() == block) {
                            blocks.add(pos);
                        }
                    }
                }
            }
        }

        return blocks;
    }
}
