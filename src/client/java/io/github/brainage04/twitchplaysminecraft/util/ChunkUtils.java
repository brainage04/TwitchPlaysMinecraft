package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

public class ChunkUtils {
    public static <T extends BlockEntity> List<T> getBlockEntities(BlockEntityType<T> type, ClientWorld world) {
        List<T> blockEntities = new ArrayList<>();

        for (int i = 0; i < world.getChunkManager().getLoadedChunkCount(); i++) {
            Chunk chunk = world.getChunkManager().chunks.chunks.get(i);

            for (BlockEntity blockEntity : chunk.blockEntities.values()) {
                if (blockEntity.getType() == type) {
                    blockEntities.add((T) blockEntity);
                }
            }
        }

        return blockEntities;
    }
}
