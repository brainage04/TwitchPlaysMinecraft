package io.github.brainage04.twitchplaysminecraft.command.move;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.BlockUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

// i can't believe i got this to work
public class FindPathCommand {
    public static int execute(FabricClientCommandSource source, BlockPos target) {
        // check if target block is actually loaded
        Chunk targetChunk = source.getWorld().getChunk(target);
        if (source.getWorld().isChunkLoaded(targetChunk.getPos().x, targetChunk.getPos().z)) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("%s is not inside of a loaded chunk!".formatted(target.toShortString()))
                    .execute();

            return 0;
        }

        BlockPos start = source.getPlayer().getBlockPos();

        while (source.getWorld().getBlockState(start).isSolidBlock(source.getWorld(), start)) {
            start = start.add(0, 1, 0);
        }

        if (start.getSquaredDistance(target) > 100 * 100) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("%s is more than 100 blocks away! Please try finding a shorter distance path.".formatted(target.toShortString()))
                    .execute();

            return 0;
        }

        List<BlockPos> path = findPath(start, target, source.getWorld());

        if (path != null && !path.isEmpty()) {
            source.sendFeedback(Text.literal("Found path with " + path.size() + " steps"));
            for (BlockPos pos : path) {
                source.getClient().worldRenderer.addParticle(
                        ParticleTypes.FLAME,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        0, 0, 0
                );
            }
            return 1;
        } else {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No path found to %s.".formatted(target.toShortString()))
                    .execute();

            return 0;
        }
    }

    public static List<BlockPos> findPath(BlockPos start, BlockPos target, World world) {
        if (world.getBlockState(target).isSolidBlock(world, target)) return null;

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Map<BlockPos, Node> allNodes = new HashMap<>();
        Set<BlockPos> closedSet = new HashSet<>();

        Node startNode = new Node(start, null, 0, heuristic(start, target));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.pos.equals(target)) {
                return reconstructPath(current);
            }

            closedSet.add(current.pos);

            for (BlockPos neighbor : getNeighbors(current.pos, world)) {
                if (closedSet.contains(neighbor)) continue;

                double moveCost = calculateMoveCost(current.pos, neighbor, target);
                double newGCost = current.gCost + moveCost;

                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor));

                if (!openSet.contains(neighborNode) || newGCost < neighborNode.gCost) {
                    neighborNode.cameFrom = current;
                    neighborNode.gCost = newGCost;
                    neighborNode.fCost = newGCost + heuristic(neighbor, target);
                    allNodes.put(neighbor, neighborNode);
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }

        return null;
    }

    private static List<BlockPos> getNeighbors(BlockPos pos, World world) {
        List<BlockPos> neighbors = new ArrayList<>();
        BlockPos[] horizontal = {
                pos.north(), pos.south(), pos.east(), pos.west()
        };

        for (BlockPos neighbor : horizontal) {
            if (BlockUtils.isWalkable(neighbor, world)) {
                neighbors.add(neighbor);
            }

            BlockPos up = neighbor.up();
            if (neighbor.getY() == pos.getY() &&
                    BlockUtils.isWalkable(up, world) &&
                    world.getBlockState(neighbor).isSolidBlock(world, neighbor)) {
                neighbors.add(up);
            }

            BlockPos down = neighbor.down();
            if (BlockUtils.isWalkable(down, world)) {
                neighbors.add(down);
            }
        }
        return neighbors;
    }

    private static double calculateMoveCost(BlockPos from, BlockPos to, BlockPos target) {
        // Base movement cost
        double baseCost = (to.getY() != from.getY()) ? 1.5 : 1.0;

        // Direction bias: penalize moves away from target
        double dx = target.getX() - from.getX();
        double dz = target.getZ() - from.getZ();
        double toDx = to.getX() - from.getX();
        double toDz = to.getZ() - from.getZ();

        // Dot product to measure alignment with target direction
        double dot = (dx * toDx + dz * toDz);
        double penalty = (dot < 0) ? 2.0 : 0.0; // Heavy penalty for moving opposite

        return baseCost + penalty;
    }

    private static double heuristic(BlockPos a, BlockPos b) {
        // Weighted Euclidean distance to strongly favor direct paths
        double dx = Math.abs(a.getX() - b.getX());
        double dy = Math.abs(a.getY() - b.getY());
        double dz = Math.abs(a.getZ() - b.getZ());
        return Math.sqrt(dx * dx + dy * dy * 2 + dz * dz); // Double weight on Y to minimize jumps
    }

    private static List<BlockPos> reconstructPath(Node endNode) {
        List<BlockPos> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(current.pos);
            current = current.cameFrom;
        }
        Collections.reverse(path);
        return path;
    }

    private static class Node {
        BlockPos pos;
        Node cameFrom;
        double gCost;
        double fCost;

        Node(BlockPos pos) {
            this.pos = pos;
        }

        Node(BlockPos pos, Node cameFrom, double gCost, double hCost) {
            this.pos = pos;
            this.cameFrom = cameFrom;
            this.gCost = gCost;
            this.fCost = gCost + hCost;
        }
    }
}