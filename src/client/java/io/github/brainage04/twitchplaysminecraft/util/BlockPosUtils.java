package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.util.math.BlockPos;

public class BlockPosUtils {
    public static boolean compareBlockPos(BlockPos first, BlockPos second) {
        return first.getX() == second.getX() && first.getY() == second.getY() && first.getZ() == second.getZ();
    }
}
