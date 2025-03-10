package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

public class LocateUtils {
    public static boolean dimensionsDoNotMatch(FabricClientCommandSource source, RegistryKey<World> desired, RegistryKey<World> actual) {
        if (desired != actual) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("This structure can only be found in the %s dimension but you are in the %s dimension!".formatted(
                            StringUtils.snakeCaseToHumanReadable(desired.getValue().getPath(), true, false),
                            StringUtils.snakeCaseToHumanReadable(actual.getValue().getPath(), true, false)
                    ))
                    .execute();

            return true;
        }

        return false;
    }

    public static BlockPos locateVillage(FabricClientCommandSource source) {
        ClientWorld world = source.getWorld();
        if (dimensionsDoNotMatch(source, World.OVERWORLD, world.getRegistryKey())) return null;

        // check for villagers near bells
        List<BellBlockEntity> bells = ChunkUtils.getBlockEntities(BlockEntityType.BELL, world);

        for (BellBlockEntity bell : bells) {
            List<VillagerEntity> villagerEntities = EntityUtils.getEntities(EntityType.VILLAGER, source.getWorld(), bell.getPos().toCenterPos(), 32, source.getPlayer().getUuid());

            if (!villagerEntities.isEmpty()) return bell.getPos();
        }

        return null;
    }

    public static BlockPos locateLavaPool(FabricClientCommandSource source) {
        ClientWorld world = source.getWorld();
        if (dimensionsDoNotMatch(source, World.OVERWORLD, world.getRegistryKey())) return null;

        for (int i = 0; i < world.getChunkManager().getLoadedChunkCount(); i++) {
            Chunk chunk = world.getChunkManager().chunks.chunks.get(i);
            if (chunk == null) continue;

            for (int x = 0; x < 15; x++) {
                for (int z = 0; z < 15; z++) {
                    int y = world.getTopYInclusive();
                    BlockPos pos = new BlockPos(chunk.getPos().getStartPos()).add(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    while (state.getBlock() == Blocks.AIR) {
                        y--;
                        pos = new BlockPos(chunk.getPos().getStartPos()).add(x, y, z);
                        state = world.getBlockState(pos);
                    }

                    if (state.getBlock() == Blocks.LAVA) return pos;

                    // if block below is air, go down from there until non-air block is reached
                    pos.add(0, -1, 0);
                    if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
                        while (world.getBlockState(pos).getBlock() == Blocks.AIR) {
                            pos.add(0, -1, 0);
                        }

                        if (state.getBlock() == Blocks.LAVA) return pos;
                    }
                }
            }
        }

        return null;
    }

    public static BlockPos locateRuinedPortal(FabricClientCommandSource source) {
        ClientWorld world = source.getWorld();

        List<ChestBlockEntity> chests = ChunkUtils.getBlockEntities(BlockEntityType.CHEST, world);

        for (ChestBlockEntity chest : chests) {
            int radius = 1;
            int minX = chest.getPos().getX() - radius;
            int maxX = chest.getPos().getX() + radius;
            int minY = chest.getPos().getY() - radius;
            int maxY = chest.getPos().getY() + radius;
            int minZ = chest.getPos().getZ() - radius;
            int maxZ = chest.getPos().getZ() + radius;

            List<Block> neighbours = new ArrayList<>(26);

            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        neighbours.add(world.getBlockState(new BlockPos(x, y, z)).getBlock());
                    }
                }
            }

            for (Block block : new Block[]{Blocks.MAGMA_BLOCK, Blocks.NETHERRACK, Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.LAVA}) {
                if (neighbours.contains(block)) return chest.getPos();
            }
        }

        return null;
    }

    public static BlockPos locateBastion(FabricClientCommandSource source) {
        ClientWorld world = source.getWorld();
        if (dimensionsDoNotMatch(source, World.NETHER, world.getRegistryKey())) return null;

        List<ChestBlockEntity> chests = ChunkUtils.getBlockEntities(BlockEntityType.CHEST, world);

        for (ChestBlockEntity chest : chests) {
            BlockPos underChestPos = chest.getPos().add(0, -1, 0);
            Block underChestBlock = world.getBlockState(underChestPos).getBlock();

            if (Registries.BLOCK.getId(underChestBlock).getPath().contains("blackstone")) {
                return chest.getPos();
            }
        }

        return null;
    }

    public static BlockPos getSpawnerPosition(FabricClientCommandSource source, EntityType<?> spawnerEntityType) {
        for (int i = 0; i < source.getWorld().getChunkManager().getLoadedChunkCount(); i++) {
            Chunk chunk = source.getWorld().getChunkManager().chunks.chunks.get(i);
            if (chunk == null) continue;

            for (BlockEntity blockEntity : chunk.blockEntities.values()) {
                if (blockEntity instanceof MobSpawnerBlockEntity spawner) {
                    if (spawner.getLogic().spawnEntry == null) continue;
                    if (spawner.getLogic().spawnEntry.entity().getString("id").isEmpty()) continue;

                    String desiredId = spawner.getLogic().spawnEntry.entity().getString("id");
                    String actualId = Registries.ENTITY_TYPE.getId(spawnerEntityType).toString();

                    if (desiredId.equals(actualId)) return blockEntity.getPos();
                }
            }
        }

        return null;
    }

    public static BlockPos locateNetherFortress(FabricClientCommandSource source) {
        if (dimensionsDoNotMatch(source, World.NETHER, source.getWorld().getRegistryKey())) return null;

        return getSpawnerPosition(source, EntityType.BLAZE);
    }

    public static BlockPos locateEndPortal(FabricClientCommandSource source) {
        if (dimensionsDoNotMatch(source, World.OVERWORLD, source.getWorld().getRegistryKey())) return null;

        return getSpawnerPosition(source, EntityType.SILVERFISH);
    }
}
