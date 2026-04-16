package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class NormalAquiferStage extends GenerateStage {

    public static final String NAME = "normal_aquifer";

    private static final int CHUNK_SIZE = 16;
    private static final int CHUNK_MASK = CHUNK_SIZE - 1;
    private static final int XZ_STRIDE = CHUNK_SIZE * CHUNK_SIZE;
    private static final double CAVE_DENSITY_TOLERANCE = 0.03d;
    private static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();

    private static final int[] NEIGHBOR_X = {1, -1, 0, 0, 0, 0};
    private static final int[] NEIGHBOR_Y = {0, 0, 0, 0, 1, -1};
    private static final int[] NEIGHBOR_Z = {0, 0, 1, -1, 0, 0};

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        DensityFunction caveDetector = ((NormalObjectHolder) level.getGeneratorObjectHolder())
                .getTerrainHolder()
                .getCaveDetector();

        int minY = level.getMinHeight();
        int maxY = SEA_LEVEL;
        int yRange = maxY - minY + 1;
        if (yRange <= 0) {
            return;
        }

        boolean[] visited = new boolean[yRange * XZ_STRIDE];
        IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        seedSurfaceWater(chunk, caveDetector, minY, maxY, visited, queue, chunkX, chunkZ);
        seedFromNeighborChunks(level, chunk, minY, maxY, visited, queue, chunkX, chunkZ);
        floodConnectedWater(chunk, minY, maxY, visited, queue);
        chunk.recalculateHeightMap();
    }

    private static void seedSurfaceWater(
            IChunk chunk,
            DensityFunction caveDetector,
            int minY,
            int maxY,
            boolean[] visited,
            IntArrayFIFOQueue queue,
            int chunkX,
            int chunkZ
    ) {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            int worldX = (chunkX << 4) + x;
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int worldZ = (chunkZ << 4) + z;
                boolean waterBiome = isWaterBiome(chunk.getBiomeId(x, maxY, z));
                boolean normalSurfaceSeed = chunk.getHeightMap(x, z) < maxY
                        && !isCaveOrEntranceAtSeaLevel(worldX, worldZ, caveDetector);
                if (!waterBiome && !normalSurfaceSeed) {
                    continue;
                }

                if (canFill(chunk.getBlockState(x, maxY, z))) {
                    chunk.setBlockState(x, maxY, z, WATER);
                    enqueue(x, maxY, z, minY, visited, queue);
                }
            }
        }
    }

    private static void seedFromNeighborChunks(
            Level level,
            IChunk chunk,
            int minY,
            int maxY,
            boolean[] visited,
            IntArrayFIFOQueue queue,
            int chunkX,
            int chunkZ
    ) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            IChunk adjacent = getGeneratedChunk(level, chunkX + face.getXOffset(), chunkZ + face.getZOffset());
            if (adjacent == null) {
                continue;
            }

            seedFromNeighborFace(adjacent, chunk, face, minY, maxY, visited, queue);
        }
    }

    private static void seedFromNeighborFace(
            IChunk adjacent,
            IChunk current,
            BlockFace face,
            int minY,
            int maxY,
            boolean[] visited,
            IntArrayFIFOQueue queue
    ) {
        for (int axis = 0; axis < CHUNK_SIZE; axis++) {
            int adjacentX;
            int adjacentZ;
            int currentX;
            int currentZ;

            switch (face) {
                case WEST -> {
                    adjacentX = CHUNK_MASK;
                    adjacentZ = axis;
                    currentX = 0;
                    currentZ = axis;
                }
                case EAST -> {
                    adjacentX = 0;
                    adjacentZ = axis;
                    currentX = CHUNK_MASK;
                    currentZ = axis;
                }
                case NORTH -> {
                    adjacentX = axis;
                    adjacentZ = CHUNK_MASK;
                    currentX = axis;
                    currentZ = 0;
                }
                case SOUTH -> {
                    adjacentX = axis;
                    adjacentZ = 0;
                    currentX = axis;
                    currentZ = CHUNK_MASK;
                }
                default -> {
                    continue;
                }
            }

            if (adjacent.getBlockState(adjacentX, maxY, adjacentZ) != WATER) {
                continue;
            }

            if (canFill(current.getBlockState(currentX, maxY, currentZ))) {
                current.setBlockState(currentX, maxY, currentZ, WATER);
                enqueue(currentX, maxY, currentZ, minY, visited, queue);
            }
        }
    }

    private static void floodConnectedWater(
            IChunk chunk,
            int minY,
            int maxY,
            boolean[] visited,
            IntArrayFIFOQueue queue
    ) {
        while (!queue.isEmpty()) {
            int packed = queue.dequeueInt();
            int x = unpackX(packed);
            int y = unpackY(packed, minY);
            int z = unpackZ(packed);

            for (int i = 0; i < 6; i++) {
                int nx = x + NEIGHBOR_X[i];
                int ny = y + NEIGHBOR_Y[i];
                int nz = z + NEIGHBOR_Z[i];

                if (nx < 0 || nx >= CHUNK_SIZE || nz < 0 || nz >= CHUNK_SIZE || ny < minY || ny > maxY) {
                    continue;
                }

                int nextPacked = pack(nx, ny, nz, minY);
                if (visited[nextPacked]) {
                    continue;
                }

                BlockState state = chunk.getBlockState(nx, ny, nz);
                if (state == WATER) {
                    visited[nextPacked] = true;
                    queue.enqueue(nextPacked);
                    continue;
                }

                if (canFill(state)) {
                    chunk.setBlockState(nx, ny, nz, WATER);
                    visited[nextPacked] = true;
                    queue.enqueue(nextPacked);
                }
            }
        }
    }

    private static boolean canFill(BlockState state) {
        var block = state.toBlock();
        return block.canBeReplaced() || !block.isSolid();
    }

    private static boolean isCaveOrEntranceAtSeaLevel(int worldX, int worldZ, DensityFunction caveDetector) {
        double density = caveDetector.compute(new DensityFunction.SinglePointContext(worldX, SEA_LEVEL, worldZ));
        return density <= CAVE_DENSITY_TOLERANCE;
    }

    private static boolean isWaterBiome(int biomeId) {
        var definition = Registries.BIOME.get(biomeId);
        if (definition == null) {
            return false;
        }
        var tags = definition.getTags();
        return tags.contains(BiomeTags.OCEAN) || tags.contains(BiomeTags.RIVER);
    }

    private static IChunk getGeneratedChunk(Level level, int chunkX, int chunkZ) {
        IChunk chunk = level.getChunk(chunkX, chunkZ, false);
        if (chunk == null || !chunk.isGenerated()) {
            return null;
        }
        return chunk;
    }

    private static void enqueue(int x, int y, int z, int minY, boolean[] visited, IntArrayFIFOQueue queue) {
        int packed = pack(x, y, z, minY);
        if (!visited[packed]) {
            visited[packed] = true;
            queue.enqueue(packed);
        }
    }

    private static int pack(int x, int y, int z, int minY) {
        return ((y - minY) * XZ_STRIDE) | ((z & CHUNK_MASK) << 4) | (x & CHUNK_MASK);
    }

    private static int unpackX(int packed) {
        return packed & CHUNK_MASK;
    }

    private static int unpackY(int packed, int minY) {
        return (packed / XZ_STRIDE) + minY;
    }

    private static int unpackZ(int packed) {
        return (packed >>> 4) & CHUNK_MASK;
    }

    @Override
    public String name() {
        return NAME;
    }
}
