package cn.nukkit.level.generator.stages;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

public class BiomeMapStage extends GenerateStage {

    public static final String NAME = "biome";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        final int minHeight = level.getMinHeight();
        final int maxHeight = level.getMaxHeight();
        for(int _x = 0; _x < 16; _x++) {
            int x = chunkX * 16 + _x;
            for(int _z = 0; _z < 16; _z++) {
                int z = chunkZ * 16 + _z;
                int biome = level.pickBiome(x, z);
                for (int y = minHeight; y <= maxHeight; y++) {
                    chunk.setBiomeId(_x, y, _z, biome);
                }
            }
        }
    }



    @Override
    public String name() {
        return NAME;
    }
}
