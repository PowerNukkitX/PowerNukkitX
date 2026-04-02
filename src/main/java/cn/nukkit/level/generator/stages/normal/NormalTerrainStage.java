package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.densityfunction.*;
import cn.nukkit.level.generator.holder.NormalObjectHolder;

public class NormalTerrainStage extends GenerateStage {

    private final static BlockState STONE = BlockStone.PROPERTIES.getDefaultState();
    private final static BlockState DEEPSLATE = BlockDeepslate.PROPERTIES.getDefaultState();

    public static final String NAME = "normal_terrain";

    public static final int SEA_LEVEL = 63;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        DensityFunction densityFunction = ((NormalObjectHolder) level.getGeneratorObjectHolder())
                .getTerrainHolder()
                .getDensityFunction();
        for (int x = 0; x < 16; x++) {
            int worldX = (chunk.getX() << 4) + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = (chunk.getZ() << 4) + z;
                for (int y = level.getMaxHeight() - 1; y >= level.getMinHeight(); y--) {
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
