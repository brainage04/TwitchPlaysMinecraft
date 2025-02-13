package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeDetector {
    public static final TagKey<Block> LOGS_TAG = TagKey.of(Registries.BLOCK.getKey(), Identifier.ofVanilla("logs"));
    public static final TagKey<Block> LEAVES_TAG = TagKey.of(Registries.BLOCK.getKey(), Identifier.ofVanilla("leaves"));

    public static TreeSet<BlockPos> getLogsInTree(TreeSet<BlockPos> logs, World world, BlockPos start) {
        int leavesCount = 0;
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();

            // Check all 26 neighboring positions in a 3×3×3 cube
            for (int dy = -1; dy <= 1; dy++) { // y is DELIBERATELY out of order (specifically first) to help with sorting by Y level
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue; // Skip the current block

                        BlockPos neighbor = pos.add(dx, dy, dz);

                        if (world.getBlockState(neighbor).isIn(LOGS_TAG)) {
                            logs.add(neighbor);
                        } else if (world.getBlockState(neighbor).isIn(LEAVES_TAG)) {
                            leavesCount++;
                        }

                        // Skip already visited blocks
                        if (visited.contains(neighbor)) continue;

                        // Add to queue for further exploration
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }

        if (logs.size() < 4 || leavesCount < 1) {
            logs.clear();
        }

        return logs;
    }
}
