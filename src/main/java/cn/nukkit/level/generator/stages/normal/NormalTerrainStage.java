package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.densityfunction.DensityCommon;
import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.material.MultiMaterial;
import cn.nukkit.utils.random.NukkitRandom;
public class NormalTerrainStage extends GenerateStage {

    private static final BlockState DEEPSLATE = BlockDeepslate.PROPERTIES.getDefaultState();
    private static final BlockState BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();

    public static final String NAME = "normal_terrain";

    public static final int SEA_LEVEL = 63;
    private final ThreadLocal<NukkitRandom> random = ThreadLocal.withInitial(NukkitRandom::new);

    @Override
    public void apply(ChunkGenerateContext context) {
        final IChunk chunk = context.getChunk();
        final Level level = chunk.getLevel();
        final NormalObjectHolder.TerrainHolder terrainHolder = ((NormalObjectHolder) level.getGeneratorObjectHolder())
                .getTerrainHolder();
        final MultiMaterial multiMaterial = terrainHolder.getMultiMaterial();
        final int minY = level.getMinHeight();
        final int maxY = level.getMaxHeight() - 1;
        final int yBlockSize = level.getMaxHeight() - minY;
        final int yCount = maxY - minY + 1;
        final int chunkBaseX = chunk.getX() << 4;
        final int chunkBaseZ = chunk.getZ() << 4;
        final DensityCommon.ChunkCache chunkCache = DensityCommon.chunkCache(chunk);
        chunkCache.clear();
        final NukkitRandom random = this.random.get();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunk.getX(), chunk.getZ()));
        final ColumnContextProvider columnContextProvider = new ColumnContextProvider(minY, yCount, chunkCache);

        terrainHolder.beginAquifer(chunk, level, chunkCache, minY, yBlockSize, SEA_LEVEL);
        try {
            chunk.batchProcess(unsafeChunk -> {
                for (int x = 0; x < 16; x++) {
                    final int worldX = chunkBaseX + x;
                    for (int z = 0; z < 16; z++) {
                        final int worldZ = chunkBaseZ + z;
                        columnContextProvider.setColumn(worldX, worldZ);
                        for (int index = yCount - 1; index >= 0; index--) {
                            final DensityFunction.FunctionContext pointContext = columnContextProvider.forIndex(index);
                            BlockState generatedState = multiMaterial == null ? null : multiMaterial.calculate(pointContext);
                            if (generatedState != null) {
                                final int y = minY + index;
                                if (generatedState.getIdentifier().equals(BlockID.STONE) && shouldPlaceDeepslate(random, y)) {
                                    generatedState = DEEPSLATE;
                                }
                                unsafeChunk.setBlockState(x, y, z, generatedState, 0);
                            }
                        }
                        unsafeChunk.setBlockState(x, minY, z, BEDROCK, 0);
                        for (int i = 0; i < random.nextBoundedInt(6); i++) {
                            int y = minY + i;
                            BlockState state = unsafeChunk.getBlockState(x, y, z);
                            if (state != BlockAir.STATE) {
                                unsafeChunk.setBlockState(x, y, z, BEDROCK, 0);
                            }
                        }
                    }
                }
            });
        } finally {
            terrainHolder.endAquifer();
            DensityCommon.releaseChunkCache(chunk);
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
        private final ColumnFunctionContext context;
        private int worldX;
        private int worldZ;

        private ColumnContextProvider(int minY, int yCount, DensityCommon.ChunkCache chunkCache) {
            this.minY = minY;
            this.yCount = yCount;
            this.context = new ColumnFunctionContext(chunkCache);
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

    private static final class ColumnFunctionContext implements DensityCommon.ChunkCacheContext {
        private final DensityCommon.ChunkCache chunkCache;
        private int worldX;
        private int worldY;
        private int worldZ;

        private ColumnFunctionContext(DensityCommon.ChunkCache chunkCache) {
            this.chunkCache = chunkCache;
        }

        private ColumnFunctionContext set(int worldX, int worldY, int worldZ) {
            this.worldX = worldX;
            this.worldY = worldY;
            this.worldZ = worldZ;
            return this;
        }

        @Override
        public int blockX() {
            return worldX;
        }

        @Override
        public int blockY() {
            return worldY;
        }

        @Override
        public int blockZ() {
            return worldZ;
        }

        @Override
        public DensityCommon.ChunkCache densityChunkCache() {
            return chunkCache;
        }
    }

}
