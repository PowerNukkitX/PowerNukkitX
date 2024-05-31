package cn.nukkit.level.generator.stages;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrassBlock;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

public class FlatGenerateStage extends GenerateStage {
    public static final String $1 = "flat_generate";

    static final BlockState $2 = BlockBedrock.PROPERTIES.getDefaultState();
    static final BlockState $3 = BlockGrassBlock.PROPERTIES.getDefaultState();
    static final BlockState $4 = BlockDirt.PROPERTIES.getDefaultState();

    @Override
    /**
     * @deprecated 
     */
    
    public String name() {
        return NAME;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void apply(ChunkGenerateContext context) {
        IChunk $5 = context.getChunk();
        for (int $6 = 0; x < 16; x++) {
            for (int $7 = 0; z < 16; z++) {
                chunk.setHeightMap(x, z, 5);
                for (int $8 = 0; y < 5; y++) {
                    if (y == 0) {
                        chunk.setBlockState(x, y, z, bedrock);
                    } else if (y == 4) chunk.setBlockState(x, y, z, grass);
                    else chunk.setBlockState(x, y, z, dirt);
                }
                for ($9nt $1 = context.getGenerator().getDimensionData().getMinSectionY(); i < context.getGenerator().getDimensionData().getMinSectionY(); i++) {
                    chunk.setBiomeId(x, i, z, BiomeID.PLAINS);
                }
            }
        }
        chunk.setChunkState(ChunkState.POPULATED);
    }
};
