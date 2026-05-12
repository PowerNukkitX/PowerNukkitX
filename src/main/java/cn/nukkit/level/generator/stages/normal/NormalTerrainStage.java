package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.UnsafeChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.densityfunction.DensityCommon;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.material.MultiMaterial;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.ArrayDeque;

public class NormalTerrainStage extends GenerateStage {

    private static final long STONE_HASH = BlockStone.PROPERTIES.getDefaultState().blockStateHash();
    private static final BlockState DEEPSLATE = BlockDeepslate.PROPERTIES.getDefaultState();
    private static final BlockState BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();

    public static final String NAME = "normal_terrain";

    public static final int SEA_LEVEL = 63;
    private static final int CELL_XZ_SIZE = 4;
    private static final int CELL_HEIGHT = 8;
    private static final int CELL_X_COUNT = 16 / CELL_XZ_SIZE;
    private static final int CELL_Z_COUNT = 16 / CELL_XZ_SIZE;
    private static final int CORNER_FLOOD_SEED_MAX_Y = 192;
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
        final DensityCommon.CellFunctionContext functionContext = new DensityCommon.CellFunctionContext(chunkCache);
        final int cellYCount = ((cellMaxY - cellMinY) / CELL_HEIGHT) + 1;
        final int[][] mandatoryTopY = new int[16][16];
        for (int x = 0; x < 16; x++) {
            final int worldX = chunkBaseX + x;
            for (int z = 0; z < 16; z++) {
                final int worldZ = chunkBaseZ + z;
                final int upper = (int) Math.ceil(
                        terrainHolder.getPreliminarySurfaceUpperBound()
                                .compute(functionContext.set(worldX, 0, worldZ))
                );
                mandatoryTopY[x][z] = Math.min(maxY, Math.max(SEA_LEVEL, upper));
            }
        }

        terrainHolder.beginAquifer(chunk, level, chunkCache, minY, yBlockSize, SEA_LEVEL);
        try {
            chunk.batchProcess(unsafeChunk -> {
                final boolean[] queued = new boolean[CELL_X_COUNT * cellYCount * CELL_Z_COUNT];
                final boolean[] solidMandatoryCells = new boolean[queued.length];
                final ArrayDeque<Integer> queue = new ArrayDeque<>();

                for (int cellYIndex = 0; cellYIndex < cellYCount; cellYIndex++) {
                    final int cellY = cellMinY + cellYIndex * CELL_HEIGHT;
                    for (int cellXIndex = 0; cellXIndex < CELL_X_COUNT; cellXIndex++) {
                        for (int cellZIndex = 0; cellZIndex < CELL_Z_COUNT; cellZIndex++) {
                            final int cellIndex = cellIndex(cellXIndex, cellYIndex, cellZIndex);
                            final int cellX = cellXIndex * CELL_XZ_SIZE;
                            final int cellZ = cellZIndex * CELL_XZ_SIZE;
                            if (!shouldGenerateMandatoryCell(mandatoryTopY, cellX, cellY, cellZ)
                                    && !isCornerFloodSeedCell(cellXIndex, cellYIndex, cellY, cellZIndex)) {
                                continue;
                            }
                            queued[cellIndex] = true;
                            solidMandatoryCells[cellIndex] = generateCell(
                                    unsafeChunk,
                                    level,
                                    terrainHolder,
                                    multiMaterial,
                                    functionContext,
                                    random,
                                    chunkBaseX,
                                    chunkBaseZ,
                                    minY,
                                    maxY,
                                    cellX,
                                    cellY,
                                    cellZ
                            );
                        }
                    }
                }

                for (int cellIndex = 0; cellIndex < solidMandatoryCells.length; cellIndex++) {
                    if (!solidMandatoryCells[cellIndex]) {
                        continue;
                    }
                    final int cellXIndex = cellIndex % CELL_X_COUNT;
                    final int cellZIndex = (cellIndex / CELL_X_COUNT) % CELL_Z_COUNT;
                    final int cellYIndex = cellIndex / (CELL_X_COUNT * CELL_Z_COUNT);
                    enqueueNeighbors(queue, queued, cellXIndex, cellYIndex, cellZIndex, cellYCount);
                }

                while (!queue.isEmpty()) {
                    final int cellIndex = queue.removeFirst();
                    final int cellXIndex = cellIndex % CELL_X_COUNT;
                    final int cellZIndex = (cellIndex / CELL_X_COUNT) % CELL_Z_COUNT;
                    final int cellYIndex = cellIndex / (CELL_X_COUNT * CELL_Z_COUNT);
                    final int cellX = cellXIndex * CELL_XZ_SIZE;
                    final int cellZ = cellZIndex * CELL_XZ_SIZE;
                    final int cellY = cellMinY + cellYIndex * CELL_HEIGHT;

                    if (generateCell(
                            unsafeChunk,
                            level,
                            terrainHolder,
                            multiMaterial,
                            functionContext,
                            random,
                            chunkBaseX,
                            chunkBaseZ,
                            minY,
                            maxY,
                            cellX,
                            cellY,
                            cellZ
                    )) {
                        enqueueNeighbors(queue, queued, cellXIndex, cellYIndex, cellZIndex, cellYCount);
                    }
                }

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        unsafeChunk.setBlockState(x, minY, z, BEDROCK, 0);
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

    private static boolean generateCell(
            UnsafeChunk unsafeChunk,
            Level level,
            NormalObjectHolder.TerrainHolder terrainHolder,
            MultiMaterial multiMaterial,
            DensityCommon.CellFunctionContext functionContext,
            NukkitRandom random,
            int chunkBaseX,
            int chunkBaseZ,
            int minY,
            int maxY,
            int cellX,
            int cellY,
            int cellZ
    ) {
        boolean hasNonAir = false;
        for (int localX = 0; localX < CELL_XZ_SIZE; localX++) {
            final int x = cellX + localX;
            final int worldX = chunkBaseX + x;
            for (int localZ = 0; localZ < CELL_XZ_SIZE; localZ++) {
                final int z = cellZ + localZ;
                final int worldZ = chunkBaseZ + z;
                for (int localY = CELL_HEIGHT - 1; localY >= 0; localY--) {
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
                        if (terrainHolder.getAquifer().get().shouldScheduleFluidUpdate()) {
                            level.scheduleUpdate(generatedState.toBlock(new Position(worldX, y, worldZ, level)), 10);
                        }
                        if (generatedState != BlockAir.STATE) {
                            hasNonAir = true;
                            if (y > unsafeChunk.getHeightMap(x, z)) {
                                unsafeChunk.setHeightMap(x, z, y);
                            }
                        }
                    }
                }
            }
        }
        return hasNonAir;
    }

    private static boolean shouldGenerateMandatoryCell(int[][] mandatoryTopY, int cellX, int cellY, int cellZ) {
        if (cellY + CELL_HEIGHT - 1 <= SEA_LEVEL) {
            return true;
        }
        for (int localX = 0; localX < CELL_XZ_SIZE; localX++) {
            final int x = cellX + localX;
            for (int localZ = 0; localZ < CELL_XZ_SIZE; localZ++) {
                final int z = cellZ + localZ;
                if (cellY <= mandatoryTopY[x][z]) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isCornerFloodSeedCell(int cellXIndex, int cellYIndex, int cellY, int cellZIndex) {
        if (cellY > CORNER_FLOOD_SEED_MAX_Y) {
            return false;
        }
        if (cellYIndex % 3 != 0) {
            return false;
        }
        return isNorthWestCornerSeed(cellXIndex, cellZIndex)
                || isNorthEastCornerSeed(cellXIndex, cellZIndex)
                || isSouthWestCornerSeed(cellXIndex, cellZIndex)
                || isSouthEastCornerSeed(cellXIndex, cellZIndex);
    }

    private static boolean isNorthWestCornerSeed(int cellXIndex, int cellZIndex) {
        return (cellXIndex == 0 && cellZIndex == 0)
                || (cellXIndex == 1 && cellZIndex == 0)
                || (cellXIndex == 0 && cellZIndex == 1);
    }

    private static boolean isNorthEastCornerSeed(int cellXIndex, int cellZIndex) {
        return (cellXIndex == CELL_X_COUNT - 1 && cellZIndex == 0)
                || (cellXIndex == CELL_X_COUNT - 2 && cellZIndex == 0)
                || (cellXIndex == CELL_X_COUNT - 1 && cellZIndex == 1);
    }

    private static boolean isSouthWestCornerSeed(int cellXIndex, int cellZIndex) {
        return (cellXIndex == 0 && cellZIndex == CELL_Z_COUNT - 1)
                || (cellXIndex == 1 && cellZIndex == CELL_Z_COUNT - 1)
                || (cellXIndex == 0 && cellZIndex == CELL_Z_COUNT - 2);
    }

    private static boolean isSouthEastCornerSeed(int cellXIndex, int cellZIndex) {
        return (cellXIndex == CELL_X_COUNT - 1 && cellZIndex == CELL_Z_COUNT - 1)
                || (cellXIndex == CELL_X_COUNT - 2 && cellZIndex == CELL_Z_COUNT - 1)
                || (cellXIndex == CELL_X_COUNT - 1 && cellZIndex == CELL_Z_COUNT - 2);
    }

    private static void enqueueCell(
            ArrayDeque<Integer> queue,
            boolean[] queued,
            int cellXIndex,
            int cellYIndex,
            int cellZIndex,
            int cellYCount
    ) {
        if (cellXIndex < 0 || cellXIndex >= CELL_X_COUNT
                || cellYIndex < 0 || cellYIndex >= cellYCount
                || cellZIndex < 0 || cellZIndex >= CELL_Z_COUNT) {
            return;
        }
        final int index = cellIndex(cellXIndex, cellYIndex, cellZIndex);
        if (queued[index]) {
            return;
        }
        queued[index] = true;
        queue.addLast(index);
    }

    private static void enqueueNeighbors(
            ArrayDeque<Integer> queue,
            boolean[] queued,
            int cellXIndex,
            int cellYIndex,
            int cellZIndex,
            int cellYCount
    ) {
        enqueueCell(queue, queued, cellXIndex + 1, cellYIndex, cellZIndex, cellYCount);
        enqueueCell(queue, queued, cellXIndex - 1, cellYIndex, cellZIndex, cellYCount);
        enqueueCell(queue, queued, cellXIndex, cellYIndex + 1, cellZIndex, cellYCount);
        enqueueCell(queue, queued, cellXIndex, cellYIndex - 1, cellZIndex, cellYCount);
        enqueueCell(queue, queued, cellXIndex, cellYIndex, cellZIndex + 1, cellYCount);
        enqueueCell(queue, queued, cellXIndex, cellYIndex, cellZIndex - 1, cellYCount);
    }

    private static int cellIndex(int cellXIndex, int cellYIndex, int cellZIndex) {
        return (cellYIndex * CELL_Z_COUNT + cellZIndex) * CELL_X_COUNT + cellXIndex;
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
}
