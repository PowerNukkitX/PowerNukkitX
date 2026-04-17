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
import cn.nukkit.level.generator.densityfunction.DensityCommon;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.material.MultiMaterial;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.Arrays;
public class NormalTerrainStage extends GenerateStage {

    private static final long STONE_HASH = BlockStone.PROPERTIES.getDefaultState().blockStateHash();
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
        final int chunkBaseX = chunk.getX() << 4;
        final int chunkBaseZ = chunk.getZ() << 4;
        final int cellMinY = Math.floorDiv(minY, 8) * 8;
        final int cellMaxY = Math.floorDiv(maxY, 8) * 8;
        final DensityCommon.ChunkCache chunkCache = DensityCommon.chunkCache(chunk);
        chunkCache.clear();
        final NukkitRandom random = this.random.get();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunk.getX(), chunk.getZ()));
        final CellFunctionContext functionContext = new CellFunctionContext(chunkCache);

        terrainHolder.beginAquifer(chunk, level, chunkCache, minY, yBlockSize, SEA_LEVEL);
        try {
            chunk.batchProcess(unsafeChunk -> {
                for (int cellX = 0; cellX < 16; cellX += 4) {
                    for (int cellZ = 0; cellZ < 16; cellZ += 4) {
                        for (int cellY = cellMaxY; cellY >= cellMinY; cellY -= 8) {
                            for (int localX = 0; localX < 4; localX++) {
                                final int x = cellX + localX;
                                final int worldX = chunkBaseX + x;
                                for (int localZ = 0; localZ < 4; localZ++) {
                                    final int z = cellZ + localZ;
                                    final int worldZ = chunkBaseZ + z;
                                    for (int localY = 7; localY >= 0; localY--) {
                                        final int y = cellY + localY;
                                        if (y < minY || y > maxY) {
                                            continue;
                                        }
                                        BlockState generatedState = multiMaterial.calculate(functionContext.set(worldX, y, worldZ));
                                        if (generatedState != null) {
                                            if (generatedState.blockStateHash() == STONE_HASH && shouldPlaceDeepslate(random, y)) {
                                                generatedState = DEEPSLATE;
                                            }
                                            unsafeChunk.setBlockState(x, y, z, generatedState, 0);
                                            if(generatedState != BlockAir.STATE) {
                                                if (y > unsafeChunk.getHeightMap(x, z)) {
                                                    unsafeChunk.setHeightMap(x, z, y);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        unsafeChunk.setBlockState(x, minY, z, BEDROCK, 0);
                        final int columnIndex = (z << 4) | x;
                        final int bedrockDepth = random.nextBoundedInt(6);
                        for (int i = 0; i < bedrockDepth; i++) {
                            final int y = minY + i;
                            final BlockState state = unsafeChunk.getBlockState(x, y, z);
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

    private static final class CellFunctionContext implements DensityCommon.ChunkCacheContext {
        private final DensityCommon.ChunkCache chunkCache;
        private int worldX;
        private int worldY;
        private int worldZ;

        private CellFunctionContext(DensityCommon.ChunkCache chunkCache) {
            this.chunkCache = chunkCache;
        }

        private CellFunctionContext set(int worldX, int worldY, int worldZ) {
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
