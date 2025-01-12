package cn.nukkit.level.generator.stages;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockSandstone;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

public class RedstoneReadyGenerateStage extends GenerateStage {
    public static final String NAME = "restone_ready_generate";

    static final BlockState sandstone = BlockSandstone.PROPERTIES.getDefaultState();
    static final BlockState bedrock = BlockBedrock.PROPERTIES.getDefaultState();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                chunk.setBlockState(x, 0, z, bedrock);

                for(int y = 1; y < 65; y++) {
                    chunk.setBlockState(x, y, z, sandstone);
                }
            }
        }

        chunk.setChunkState(ChunkState.POPULATED);
    }
};
