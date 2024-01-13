package cn.nukkit.level.generator;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;

public final class GenerateStages {
    private GenerateStages() {
    }

    public static final GenerateStage FLAT_GENERATE = new GenerateStage() {
        static final BlockState bedrock = BlockBedrock.PROPERTIES.getDefaultState();
        static final BlockState grass = BlockGrass.PROPERTIES.getDefaultState();
        static final BlockState dirt = BlockDirt.PROPERTIES.getDefaultState();

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
                }
            }
            chunk.setChunkState(ChunkState.POPULATED);
        }
    };

    public static final GenerateStage LIGHT_POPULATION = new GenerateStage() {
        @Override
        public void apply(ChunkGenerateContext context) {
            final IChunk chunk = context.getChunk();
            if (chunk == null) {
                return;
            }

            chunk.recalculateHeightMap();
            chunk.populateSkyLight();
            chunk.setLightPopulated();
            chunk.setChunkState(ChunkState.FINISHED);
        }
    };

    public static final GenerateStage FINISHED = new GenerateStage() {
        @Override
        public void apply(ChunkGenerateContext context) {
            IChunk chunk = context.getChunk();
            chunk.setChunkState(ChunkState.FINISHED);
            context.getLevel().setChunk(chunk.getX(), chunk.getZ(), chunk, false);
        }
    };
}
