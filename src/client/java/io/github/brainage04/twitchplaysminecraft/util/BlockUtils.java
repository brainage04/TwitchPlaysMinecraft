package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BlockUtils {
    public static Vec3d getFacePos(BlockPos pos, Direction face) {
        return pos.toCenterPos().add(face.getDoubleVector().multiply(0.5));
    }

    public static BlockHitResult raycastToBlockFace(ClientPlayerEntity player, Vec3d target) {
        return player.getWorld().raycast(new RaycastContext(
                player.getEyePos(), target,
                RaycastContext.ShapeType.COLLIDER, // Only hit blocks, not fluids
                RaycastContext.FluidHandling.NONE, // Ignore fluids
                player // The entity performing the raycast (needed for exclusion)
        ));
    }

    /**
     * Performs world raycasts to block faces at pos {@code pos} where applicable and determines
     * if the player can see the centers of any of the faces unobstructed by other blocks.
     * @return The Direction from which the face can be seen if the player can see the block face, {@code null} otherwise
     */
    public static Direction canSeeBlockFace(ClientPlayerEntity player, BlockPos pos) {
        Vec3d west = getFacePos(pos, Direction.WEST);
        if (player.getX() < west.getX()) {
            BlockHitResult westResult = raycastToBlockFace(player, west);
            if (pos.equals(westResult.getBlockPos())) return Direction.WEST;
        }

        Vec3d east = getFacePos(pos, Direction.EAST);
        if (player.getX() > east.getX()) {
            BlockHitResult eastResult = raycastToBlockFace(player, east);
            if (pos.equals(eastResult.getBlockPos())) return Direction.EAST;
        }

        Vec3d up = getFacePos(pos, Direction.UP);
        if (player.getY() < up.getY()) {
            BlockHitResult upResult = raycastToBlockFace(player, up);
            if (pos.equals(upResult.getBlockPos())) return Direction.UP;
        }

        Vec3d down = getFacePos(pos, Direction.DOWN);
        if (player.getY() > down.getY()) {
            BlockHitResult downResult = raycastToBlockFace(player, down);
            if (pos.equals(downResult.getBlockPos())) return Direction.DOWN;
        }

        Vec3d north = getFacePos(pos, Direction.NORTH);
        if (player.getZ() < north.getZ()) {
            BlockHitResult northResult = raycastToBlockFace(player, north);
            if (pos.equals(northResult.getBlockPos())) return Direction.NORTH;
        }

        Vec3d south = getFacePos(pos, Direction.SOUTH);
        if (player.getZ() > south.getZ()) {
            BlockHitResult southResult = raycastToBlockFace(player, south);
            if (pos.equals(southResult.getBlockPos())) return Direction.SOUTH;
        }

        return null;
    }

    public static Vec3d searchCuboid(World world, ClientPlayerEntity player, Block targetBlock, int radius, boolean outlineOnly) {
        BlockPos center = player.getBlockPos();
        double closestDistance = Double.MAX_VALUE;
        BlockPos closestPos = null;
        Direction closestFace = null;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // only check outer shell of cube
                    if (outlineOnly && Math.abs(x) != radius && Math.abs(y) != radius && Math.abs(z) != radius) continue;

                    BlockPos pos = center.add(x, y, z);
                    if (world.getBlockState(pos).getBlock() == targetBlock) {
                        // each time new closest block is found, check if the player can raycast to any of the 3 closest faces without obstruction
                        // if they can, the block is visible on their screen and it counts
                        // otherwise, ignore the block
                        double distance = center.getSquaredDistance(pos);
                        if (distance >= closestDistance) continue;
                        Direction face = canSeeBlockFace(player, pos);
                        if (face == null) continue;

                        closestDistance = distance;
                        closestPos = pos;
                        closestFace = face;
                    }
                }
            }
        }

        if (closestPos == null || closestFace == null) return null;

        return getFacePos(closestPos, closestFace); // Returns null if no matching block is found in this layer
    }
}
