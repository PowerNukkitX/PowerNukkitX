package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.level.generator.holder.NormalObjectHolder;

public class NormalTerrainStage extends GenerateStage {

    private static final BlockState STONE = BlockStone.PROPERTIES.getDefaultState();
    private static final BlockState DEEPSLATE = BlockDeepslate.PROPERTIES.getDefaultState();

    public static final String NAME = "normal_terrain";

    public static final int SEA_LEVEL = 62;

    @Override
    public void apply(ChunkGenerateContext context) {
        final IChunk chunk = context.getChunk();
        final Level level = chunk.getLevel();
        final DensityFunction densityFunction = ((NormalObjectHolder) level.getGeneratorObjectHolder())
                .getTerrainHolder()
                .getDensityFunction();
        final int minY = level.getMinHeight();
        final int maxY = level.getMaxHeight() - 1;

        for (int x = 0; x < 16; x++) {
            final int worldX = (chunk.getX() << 4) + x;
            for (int z = 0; z < 16; z++) {
                final int worldZ = (chunk.getZ() << 4) + z;
                for (int y = maxY; y >= minY; y--) {
                    double density = densityFunction.compute(new DensityFunction.SinglePointContext(worldX, y, worldZ));
                    if (density > 0.0) {
                        chunk.setBlockState(x, y, z, y < 0 ? DEEPSLATE : STONE);
                    }
                }
            }
        }
        chunk.recalculateHeightMap();
    }

    @Override
    public String name() {
        return NAME;
    }

}
