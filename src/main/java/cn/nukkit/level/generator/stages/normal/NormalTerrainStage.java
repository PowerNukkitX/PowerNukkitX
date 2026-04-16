package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.utils.random.NukkitRandom;

public class NormalTerrainStage extends GenerateStage {

    private static final BlockState STONE = BlockStone.PROPERTIES.getDefaultState();
    private static final BlockState DEEPSLATE = BlockDeepslate.PROPERTIES.getDefaultState();
    private static final BlockState BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();

    public static final String NAME = "normal_terrain";

    public static final int SEA_LEVEL = 62;
    private final ThreadLocal<NukkitRandom> random = ThreadLocal.withInitial(NukkitRandom::new);

    @Override
    public void apply(ChunkGenerateContext context) {
        final IChunk chunk = context.getChunk();
        final Level level = chunk.getLevel();
        final DensityFunction densityFunction = ((NormalObjectHolder) level.getGeneratorObjectHolder())
                .getTerrainHolder()
                .getDensityFunction();
        final int minY = level.getMinHeight();
        final int maxY = level.getMaxHeight() - 1;
        final int yCount = maxY - minY + 1;
        final int chunkBaseX = chunk.getX() << 4;
        final int chunkBaseZ = chunk.getZ() << 4;
        final NukkitRandom random = this.random.get();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunk.getX(), chunk.getZ()));
        final double[] densities = new double[yCount];
        final ColumnContextProvider columnContextProvider = new ColumnContextProvider(minY, yCount);

        for (int x = 0; x < 16; x++) {
            final int worldX = chunkBaseX + x;
            for (int z = 0; z < 16; z++) {
                final int worldZ = chunkBaseZ + z;
                columnContextProvider.setColumn(worldX, worldZ);
                densityFunction.fillArray(densities, columnContextProvider);
                for (int index = yCount - 1; index >= 0; index--) {
                    if (densities[index] > 0.0) {
                        final int y = minY + index;
                        chunk.setBlockState(x, y, z, shouldPlaceDeepslate(random, y) ? DEEPSLATE : STONE);
                    }
                }
                chunk.setBlockState(x, minY, z, BEDROCK);
                for (int i = 0; i < random.nextBoundedInt(6); i++) {
                    int y = minY + i;
                    BlockState state = chunk.getBlockState(x, y, z);
                    if (state != BlockAir.STATE) {
                        chunk.setBlockState(x, y, z, BEDROCK);
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

    private static boolean shouldPlaceDeepslate(NukkitRandom random, int y) {
        if (y < 0) {
            return true;
        }
        if (y > 8) {
            return false;
        }
        return random.nextBoundedInt(9) >= y;
    }

    private static final class ColumnContextProvider implements DensityFunction.ContextProvider {
        private final int minY;
        private final int yCount;
        private final DensityFunction.MutableFunctionContext context = new DensityFunction.MutableFunctionContext();
        private int worldX;
        private int worldZ;

        private ColumnContextProvider(int minY, int yCount) {
            this.minY = minY;
            this.yCount = yCount;
        }

        private void setColumn(int worldX, int worldZ) {
            this.worldX = worldX;
            this.worldZ = worldZ;
        }

        @Override
        public DensityFunction.FunctionContext forIndex(int index) {
            if (index < 0 || index >= yCount) {
                throw new IndexOutOfBoundsException("Density index out of bounds: " + index);
            }
            return context.set(worldX, minY + index, worldZ);
        }
    }

}
