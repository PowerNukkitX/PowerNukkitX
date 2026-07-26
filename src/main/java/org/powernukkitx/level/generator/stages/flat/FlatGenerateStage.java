package org.powernukkitx.level.generator.stages.flat;

import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockGrassBlock;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.ChunkState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;

public class FlatGenerateStage extends GenerateStage {
    public static final String NAME = "flat_generate";

    static final BlockState bedrock = BlockBedrock.PROPERTIES.getDefaultState();
    static final BlockState grass = BlockGrassBlock.PROPERTIES.getDefaultState();
    static final BlockState dirt = BlockDirt.PROPERTIES.getDefaultState();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setHeightMap(x, z, 5);
                for (int y = 0; y < 5; y++) {
                    if (y == 0) {
                        chunk.setBlockState(x, y, z, bedrock);
                    } else if (y == 4) chunk.setBlockState(x, y, z, grass);
                    else chunk.setBlockState(x, y, z, dirt);
                }
                for (int i = context.getGenerator().getDimensionData().getMinSectionY(); i < context.getGenerator().getDimensionData().getMinSectionY(); i++) {
                    chunk.setBiomeId(x, i, z, BiomeID.PLAINS);
                }
            }
        }
        chunk.setChunkState(ChunkState.POPULATED);
    }
};
