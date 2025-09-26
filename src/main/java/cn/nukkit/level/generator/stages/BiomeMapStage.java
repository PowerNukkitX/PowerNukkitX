package cn.nukkit.level.generator.stages;

import cn.nukkit.block.BlockAir;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.biome.BiomePicker;

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
        int[] biomes = new int[16*16];
        for(int _x = 0; _x < 16; _x++) {
            int x = chunkX * 16 + _x;
            for(int _z = 0; _z < 16; _z++) {
                int z = chunkZ * 16 + _z;
                int biome = biomePicker.pick(x, SEA_LEVEL, z).getBiomeId();
                biomes[_x * 16 + _z] = biome;
            }
        }
        chunk.batchProcess(unsafeChunk -> {
            for (int y = minHeight; y <= maxHeight; y++) {
                ChunkSection section = unsafeChunk.getOrCreateSection(y >> 4);
                for(int x = 0; x < 16; x++) {
                    for(int z = 0; z < 16; z++) {
                        section.setBlockState(x, y & 0x0f, z, BlockAir.STATE, 0);
                        section.setBiomeId(x, y & 0x0f, z, biomes[x * 16 + z]);
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
