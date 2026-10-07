package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;
import org.powernukkitx.level.generator.biome.result.BiomeResult;
import org.powernukkitx.level.generator.biome.result.OverworldBiomeResult;
import org.powernukkitx.level.generator.densityfunction.DensityCommon;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class BiomeMapStage extends GenerateStage {

    public static final String NAME = "biome";

    private BiomePicker biomePicker;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        if(biomePicker == null) biomePicker = level.getBiomePicker();
        final int minHeight = level.getMinHeight();
        final int maxHeight = level.getMaxHeight();
        BiomeResult[] biomes = new BiomeResult[16*16];
        if (biomePicker instanceof OverworldBiomePicker overworldBiomePicker) {
            DensityCommon.ChunkCache chunkCache = DensityCommon.chunkCache(chunk);
            chunkCache.clear();
            DensityCommon.CellFunctionContext functionContext = new DensityCommon.CellFunctionContext(chunkCache);
            try {
                for(int _x = 0; _x < 16; _x++) {
                    int x = chunkX * 16 + _x;
                    for(int _z = 0; _z < 16; _z++) {
                        int z = chunkZ * 16 + _z;
                        biomes[_x * 16 + _z] = overworldBiomePicker.pickRaw(x, SEA_LEVEL, z, functionContext.set(x, SEA_LEVEL, z));
                    }
                }
            } finally {
                DensityCommon.releaseChunkCache(chunk);
            }
        } else {
            for(int _x = 0; _x < 16; _x++) {
                int x = chunkX * 16 + _x;
                for(int _z = 0; _z < 16; _z++) {
                    int z = chunkZ * 16 + _z;
                    biomes[_x * 16 + _z] = biomePicker.pick(x, SEA_LEVEL, z);
                }
            }
        }
        int[] surfaceBiomeIds = new int[256];
        int[] caveBiomeIds = new int[256];
        int[] deepBiomeIds = new int[256];

        for (int index = 0; index < biomes.length; index++) {
            BiomeResult result = biomes[index];
            int biomeId = result.getBiomeId();
            surfaceBiomeIds[index] = biomeId;
            caveBiomeIds[index] = biomeId;
            deepBiomeIds[index] = biomeId;

            if (result instanceof OverworldBiomeResult biomeResult) {
                caveBiomeIds[index] = biomeResult.correct(-26).getBiomeId();
                biomeResult.reset();
                deepBiomeIds[index] = biomeResult.correct(-127).getBiomeId();
                biomeResult.reset();
            }
        }

        chunk.batchProcess(unsafeChunk -> {
            var assignedBiomeIds = new IntOpenHashSet();
            var featureBiomeIds = new IntOpenHashSet();
            int[] surfaceMinY = new int[256];
            int[] caveMinY = new int[256];

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int index = x * 16 + z;
                    int height = unsafeChunk.getHeightMap(x, z);
                    int surfaceStart = height - 26;
                    int caveStart = height - 127;
                    surfaceMinY[index] = surfaceStart;
                    caveMinY[index] = caveStart;

                    if (maxHeight >= surfaceStart) {
                        assignedBiomeIds.add(surfaceBiomeIds[index]);
                    }
                    if (Math.max(minHeight, caveStart) <= Math.min(maxHeight, surfaceStart - 1)) {
                        assignedBiomeIds.add(caveBiomeIds[index]);
                    }
                    if (minHeight < caveStart) {
                        assignedBiomeIds.add(deepBiomeIds[index]);
                    }

                    int featureMinY = minHeight + 1;
                    int featureMaxY = Math.min(maxHeight, height - 1);
                    if (featureMaxY >= featureMinY) {
                        if (Math.max(featureMinY, surfaceStart) <= featureMaxY) {
                            featureBiomeIds.add(surfaceBiomeIds[index]);
                        }
                        if (Math.max(featureMinY, caveStart) <= Math.min(featureMaxY, surfaceStart - 1)) {
                            featureBiomeIds.add(caveBiomeIds[index]);
                        }
                        if (featureMinY <= Math.min(featureMaxY, caveStart - 1)) {
                            featureBiomeIds.add(deepBiomeIds[index]);
                        }
                    }
                }
            }

            for (int y = maxHeight; y >= minHeight; y--) {
                ChunkSection section = unsafeChunk.getOrCreateSection(y >> 4);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int index = x * 16 + z;
                        int biomeId;

                        if (y >= surfaceMinY[index]) {
                            biomeId = surfaceBiomeIds[index];
                        } else if (y >= caveMinY[index]) {
                            biomeId = caveBiomeIds[index];
                        } else {
                            biomeId = deepBiomeIds[index];
                        }

                        section.setBiomeId(x, y & 0x0f, z, biomeId);
                    }
                }
            }

            for (int biomeId : assignedBiomeIds) {
                unsafeChunk.getBiomeState().updateBiome(biomeId);
            }

            NormalChunkFeatureStage.cacheGeneratedBiomes(chunk, featureBiomeIds);
        });
    }

    @Override
    public String name() {
        return NAME;
    }
}
