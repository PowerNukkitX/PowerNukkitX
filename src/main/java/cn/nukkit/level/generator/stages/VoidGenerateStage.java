package cn.nukkit.level.generator.stages;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

public class VoidGenerateStage extends GenerateStage {
    public static final String NAME = "void_generate";

    static final BlockState stone = BlockStone.PROPERTIES.getDefaultState();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if(chunk.getX() == 0 && chunk.getZ() == 0) {
            chunk.setBlockState(0, 4, 0, stone);
        }

        chunk.setChunkState(ChunkState.POPULATED);
    }
};
