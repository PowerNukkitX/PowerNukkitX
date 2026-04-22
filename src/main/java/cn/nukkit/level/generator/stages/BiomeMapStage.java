package cn.nukkit.level.generator.stages;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.level.generator.biome.result.BiomeResult;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.level.generator.densityfunction.DensityCommon;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

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
                        biomes[_x * 16 + _z] = overworldBiomePicker.pick(x, SEA_LEVEL, z, functionContext.set(x, SEA_LEVEL, z));
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
        chunk.batchProcess(unsafeChunk -> {
            for (int y = maxHeight; y >= minHeight; y--) {
                ChunkSection section = unsafeChunk.getOrCreateSection(y >> 4);
                for(int x = 0; x < 16; x++) {
                    for(int z = 0; z < 16; z++) {
                        BiomeResult result = biomes[x * 16 + z];
                        if(result instanceof OverworldBiomeResult biomeResult) biomeResult.correct(y - unsafeChunk.getHeightMap(x, z));
                        section.setBiomeId(x, y & 0x0f, z, result.getBiomeId());
                        if(result instanceof OverworldBiomeResult biomeResult) biomeResult.reset();
                    }
                }
            }
        });
    }

    @Override
    public String name() {
        return NAME;
    }
}
