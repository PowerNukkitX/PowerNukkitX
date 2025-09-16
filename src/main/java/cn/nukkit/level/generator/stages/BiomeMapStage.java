package cn.nukkit.level.generator.stages;

import cn.nukkit.block.BlockAir;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.biome.BiomePicker;

import java.util.ArrayList;
import java.util.List;

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
        for(int _x = 0; _x < 16; _x++) {
            int x = chunkX * 16 + _x;
            for(int _z = 0; _z < 16; _z++) {
                int z = chunkZ * 16 + _z;
                int biome = biomePicker.pick(x, SEA_LEVEL, z).getBiomeId();
                for (int y = minHeight; y <= maxHeight; y++) {
                    chunk.setBlockState(_x, y, _z, BlockAir.STATE);
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
