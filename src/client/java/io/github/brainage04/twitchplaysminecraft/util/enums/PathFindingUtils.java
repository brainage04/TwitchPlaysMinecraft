package io.github.brainage04.twitchplaysminecraft.util.enums;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.*;

public class PathFindingUtils {
    public static List<BlockPos> calculatePathToMob(ClientPlayerEntity player, LivingEntity target) {
        BlockPos start = player.getBlockPos();
        BlockPos goal = target.getBlockPos();

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(node -> node.totalCost));
        Set<BlockPos> closedList = new HashSet<>();
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
        Map<BlockPos, Double> gScore = new HashMap<>();

        openList.add(new Node(start, 0, heuristic(start, goal)));
        gScore.put(start, 0.0);

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            if (current.pos.equals(goal)) {
                return reconstructPath(cameFrom, current.pos);
            }

            closedList.add(current.pos);

            for (BlockPos neighbor : getNeighbors(current.pos, player.clientWorld)) {
                if (closedList.contains(neighbor)) continue;

                double tentativeG = gScore.getOrDefault(current.pos, Double.MAX_VALUE) +
                        (neighbor.getY() == current.pos.getY() ? 1.0 : 1.5); // Higher cost for vertical

                if (!gScore.containsKey(neighbor) || tentativeG < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current.pos);
                    gScore.put(neighbor, tentativeG);
                    double f = tentativeG + heuristic(neighbor, goal);
                    openList.add(new Node(neighbor, tentativeG, f));
                }
            }
        }
        return null; // No path found
    }

    private static List<BlockPos> getNeighbors(BlockPos pos, World world) {
        List<BlockPos> neighbors = new ArrayList<>();
        BlockPos[] directions = { pos.north(), pos.south(), pos.east(), pos.west() };

        // Horizontal movement
        for (BlockPos neighbor : directions) {
            if (isWalkable(neighbor, world)) {
                neighbors.add(neighbor);
            }
            // Jump up 1 block
            BlockPos above = neighbor.up();
            if (isWalkable(above, world) &&
                    world.getBlockState(neighbor).getCollisionShape(world, neighbor).isEmpty() &&
                    world.getBlockState(pos.up()).getCollisionShape(world, pos.up()).isEmpty()) {
                neighbors.add(above);
            }
            // Drop down 1 block
            BlockPos below = neighbor.down();
            if (isWalkable(below, world)) {
                neighbors.add(below);
            }
        }

        return neighbors;
    }

    private static boolean isWalkable(BlockPos pos, World world) {
        BlockPos below = pos.down();
        // Player needs 2 blocks of clearance above a solid floor
        return world.getBlockState(pos).getCollisionShape(world, pos).isEmpty() && // Current pos is air
                world.getBlockState(pos.up()).getCollisionShape(world, pos.up()).isEmpty() && // Above is air
                world.getBlockState(below).isSolidBlock(world, below); // Solid ground below
    }

    private static double heuristic(BlockPos a, BlockPos b) {
        return Math.sqrt(a.getSquaredDistance(b)); // Euclidean distance for better diagonal preference
    }

    private static List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos current) {
        List<BlockPos> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }

    public static void visualizePath(ClientPlayerEntity player, List<BlockPos> path) {
        for (BlockPos pos : path) {
            player.clientWorld.addParticle(
                    net.minecraft.particle.ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    0, 0, 0
            );
        }
    }

    // total cost = cost from start + heuristic
    private record Node(BlockPos pos, double costFromStart, double totalCost) {}

    public static void guidePlayerAlongPath(ClientPlayerEntity player, Vec3d nextPos) {
        player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, nextPos);

        // todo: find the combination of keys to press that will maximise movement speed?
        MinecraftClient.getInstance().options.forwardKey.setPressed(true);
    }
}
