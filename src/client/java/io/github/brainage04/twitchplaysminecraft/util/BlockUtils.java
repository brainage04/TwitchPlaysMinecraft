package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlockUtils {
    public static Vec3d getFacePos(BlockPos pos, Direction face) {
        return pos.toCenterPos().add(face.getDoubleVector().multiply(0.5));
    }

    public static BlockHitResult raycastToBlockFace(ClientPlayerEntity player, Vec3d target) {
        return player.getWorld().raycast(new RaycastContext(
                player.getEyePos(), target,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));
    }

    private record FaceResult(Direction direction, double distance) {}

    /**
     * Performs world raycasts to block faces at pos {@code pos} where applicable and determines
     * if the player can see the centers of any of the faces unobstructed by other blocks.
     * @return The Direction from which the face can be seen if the player can see the block face, {@code null} otherwise
     */
    public static Direction canSeeBlockFace(ClientPlayerEntity player, BlockPos pos) {
        List<FaceResult> visibleFaces = new ArrayList<>();

        Vec3d up = getFacePos(pos, Direction.UP);
        Vec3d down = getFacePos(pos, Direction.DOWN);
        if (player.getEyeY() > up.getY()) {
            if (up.equals(raycastToBlockFace(player, up).getPos())) visibleFaces.add(new FaceResult(Direction.UP, player.getEyePos().squaredDistanceTo(up)));
        } else if (player.getEyeY() < down.getY()) {
            if (down.equals(raycastToBlockFace(player, down).getPos())) visibleFaces.add(new FaceResult(Direction.DOWN, player.getEyePos().squaredDistanceTo(down)));
        }

        Vec3d north = getFacePos(pos, Direction.NORTH);
        Vec3d south = getFacePos(pos, Direction.SOUTH);
        if (player.getZ() < north.getZ()) {
            if (north.equals(raycastToBlockFace(player, north).getPos())) visibleFaces.add(new FaceResult(Direction.NORTH, player.getEyePos().squaredDistanceTo(north)));
        } else if (player.getZ() > south.getZ()) {
            if (south.equals(raycastToBlockFace(player, south).getPos())) visibleFaces.add(new FaceResult(Direction.SOUTH, player.getEyePos().squaredDistanceTo(south)));
        }

        Vec3d west = getFacePos(pos, Direction.WEST);
        Vec3d east = getFacePos(pos, Direction.EAST);
        if (player.getX() < west.getX()) {
            if (west.equals(raycastToBlockFace(player, west).getPos())) visibleFaces.add(new FaceResult(Direction.WEST, player.getEyePos().squaredDistanceTo(west)));
        } else if (player.getX() > east.getX()) {
            if (east.equals(raycastToBlockFace(player, east).getPos())) visibleFaces.add(new FaceResult(Direction.EAST, player.getEyePos().squaredDistanceTo(east)));
        }

        if (visibleFaces.isEmpty()) return null;

        return visibleFaces.stream()
                .min(Comparator.comparingDouble(faceResult -> faceResult.distance))
                .map(faceResult -> faceResult.direction)
                .orElse(null);
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
