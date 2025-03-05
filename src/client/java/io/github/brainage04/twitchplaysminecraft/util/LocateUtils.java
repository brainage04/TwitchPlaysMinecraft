package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;

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

    // todo: add chests and other blocks (villager workstations)
    public static BlockPos locateVillage(FabricClientCommandSource source) {
        ClientWorld world = source.getWorld();
        if (dimensionsDoNotMatch(source, World.OVERWORLD, world.getRegistryKey())) return null;

        List<VillagerEntity> villagers = EntityUtils.getEntities(VillagerEntity.class, world);

        for (VillagerEntity villager : villagers) {
            // check for special blocks in the chunk - grass path, beds
            // 3 blocks up, 3 blocks down
            Chunk chunk = world.getChunk(villager.getBlockPos());
            BlockPos chunkPos = chunk.getPos().getStartPos();

            for (int x = 0; x < 15; x++) {
                for (int y = -3; y < 3; y++) {
                    for (int z = 0; z < 15; z++) {
                        BlockPos pos = new BlockPos(chunkPos)
                                .withY(villager.getBlockY())
                                .add(x, y, z);

                        BlockState state = chunk.getBlockState(pos);
                        Block block = state.getBlock();

                        if (block == Blocks.DIRT_PATH ||
                                block instanceof BedBlock ||
                                block instanceof StairsBlock ||
                                block == Blocks.BELL) {
                            return pos;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static BlockPos locateLavaPool(FabricClientCommandSource source) {
        ClientWorld world = source.getWorld();
        if (dimensionsDoNotMatch(source, World.OVERWORLD, world.getRegistryKey())) return null;

        Registry<Biome> biomeRegistry = RegistryUtils.getRegistry(RegistryKeys.BIOME, world);
        List<Biome> appropriateBiomes = new ArrayList<>();

        for (Biome biome : biomeRegistry) {
            GenerationSettings generationSettings = biome.getGenerationSettings();

            RegistryEntryList<PlacedFeature> lakeFeatures = generationSettings.getFeatures().get(GenerationStep.Feature.LAKES.ordinal());

            for (RegistryEntry<PlacedFeature> placedFeature : lakeFeatures) {
                if (!placedFeature.matchesKey(MiscPlacedFeatures.LAKE_LAVA_SURFACE)) {
                    appropriateBiomes.add(biome);
                }
            }
        }

        for (int i = 0; i < world.getChunkManager().getLoadedChunkCount(); i++) {
            Chunk chunk = world.getChunkManager().chunks.chunks.get(i);

            for (int x = 0; x < 15; x++) {
                for (int z = 0; z < 15; z++) {
                    int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z) - 1;

                    BlockPos pos = new BlockPos(chunk.getPos().getStartPos()).add(x, y, z);
                    if (!appropriateBiomes.contains(world.getBiome(pos).value())) continue;

                    BlockState state = world.getBlockState(pos);
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

    public static BlockPos locateNetherFortress(FabricClientCommandSource source) {
        ClientWorld world = source.getWorld();
        if (dimensionsDoNotMatch(source, World.NETHER, world.getRegistryKey())) return null;

        for (int i = 0; i < world.getChunkManager().getLoadedChunkCount(); i++) {
            Chunk chunk = world.getChunkManager().chunks.chunks.get(i);

            for (BlockEntity blockEntity : chunk.blockEntities.values()) {
                if (blockEntity instanceof MobSpawnerBlockEntity spawner) {
                    if (spawner.getLogic().spawnEntry == null) continue;
                    if (spawner.getLogic().spawnEntry.entity().getString("id").isEmpty()) continue;

                    String desiredId = spawner.getLogic().spawnEntry.entity().getString("id");
                    String actualId = Registries.ENTITY_TYPE.getId(EntityType.BLAZE).toString();

                    if (desiredId.equals(actualId)) return blockEntity.getPos();
                }
            }
        }

        return null;
    }

    public static BlockPos locateEndPortal(FabricClientCommandSource source) {
        ClientWorld world = source.getWorld();
        if (dimensionsDoNotMatch(source, World.OVERWORLD, world.getRegistryKey())) return null;

        BlockPos center = source.getEntity().getBlockPos();

        int radius = 64;
        for (int x = -radius; x < radius; x++) {
            for (int y = world.getBottomY(); y < world.getSeaLevel(); y++) {
                for (int z = -radius; z < radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    if (block == Blocks.END_PORTAL_FRAME) {
                        return pos;
                    }
                }
            }
        }

        return null;
    }
}
