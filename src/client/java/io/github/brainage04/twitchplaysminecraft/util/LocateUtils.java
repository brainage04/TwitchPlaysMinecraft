package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.block.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.ArrayList;
import java.util.List;

public class LocateUtils {
    public static BlockPos locateVillage(ClientWorld world) {
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

    public static BlockPos locateLavaPool(ClientWorld world) {
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
}
