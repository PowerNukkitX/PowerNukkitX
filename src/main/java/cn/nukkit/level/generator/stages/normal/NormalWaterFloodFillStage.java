package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.math.BlockFace;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.BitSet;

public class NormalWaterFloodFillStage extends GenerateStage {

    public static final String NAME = "normal_water_flood_fill";

    private static final BlockState AIR = BlockAir.STATE;
    private static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int minY = level.getMinHeight();
        int maxY = Math.min(level.getMaxHeight() - 1, NormalTerrainStage.SEA_LEVEL);

        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
        LongOpenHashSet visited = new LongOpenHashSet();
        Long2ObjectOpenHashMap<IChunk> chunkCache = new Long2ObjectOpenHashMap<>();
        Long2ObjectOpenHashMap<BitSet> changedColumns = new Long2ObjectOpenHashMap<>();
        chunkCache.put(chunk.getIndex(), chunk);

        seedCurrentChunk(level, chunkCache, queue, visited, chunk, minY, maxY);
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            seedBorder(level, chunkCache, queue, visited, chunkX, chunkZ, face, minY, maxY);
        }

        boolean changed = false;
        while (!queue.isEmpty()) {
            long node = queue.dequeueLong();
            int x = unpackX(node);
            int y = unpackY(node);
            int z = unpackZ(node);
            changed |= tryFlood(level, chunkCache, queue, visited, changedColumns, x + 1, y, z, minY, maxY);
            changed |= tryFlood(level, chunkCache, queue, visited, changedColumns, x - 1, y, z, minY, maxY);
            changed |= tryFlood(level, chunkCache, queue, visited, changedColumns, x, y, z + 1, minY, maxY);
            changed |= tryFlood(level, chunkCache, queue, visited, changedColumns, x, y, z - 1, minY, maxY);
        }

        if (changed) {
            for (long changedChunkIndex : changedColumns.keySet()) {
                IChunk changedChunk = chunkCache.get(changedChunkIndex);
                BitSet columns = changedColumns.get(changedChunkIndex);
                if (changedChunk != null && columns != null) {
                    for (int column = columns.nextSetBit(0); column >= 0; column = columns.nextSetBit(column + 1)) {
                        changedChunk.recalculateHeightMapColumn(column & 0xF, column >>> 4);
                    }
                }
            }
        }
    }

    private static void seedCurrentChunk(Level level, Long2ObjectOpenHashMap<IChunk> chunkCache, LongArrayFIFOQueue queue, LongOpenHashSet visited,
                                         IChunk chunk, int minY, int maxY) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int worldX = chunkX << 4;
        int worldZ = chunkZ << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y <= maxY; y++) {
                    if (chunk.getBlockState(x, y, z) == WATER
                            && hasHorizontalAirNeighbor(level, chunkCache, worldX + x, y, worldZ + z)) {
                        enqueue(queue, visited, worldX + x, y, worldZ + z);
                    }
                }
            }
        }
    }

    private static void seedBorder(Level level, Long2ObjectOpenHashMap<IChunk> chunkCache, LongArrayFIFOQueue queue, LongOpenHashSet visited,
                                   int chunkX, int chunkZ, BlockFace face, int minY, int maxY) {
        int chunkOffsetX = face.getXOffset();
        int chunkOffsetZ = face.getZOffset();
        IChunk adjacent = getGeneratedChunk(level, chunkCache, chunkX + chunkOffsetX, chunkZ + chunkOffsetZ);
        IChunk current = getGeneratedChunk(level, chunkCache, chunkX, chunkZ);
        if (adjacent == null || current == null) {
            return;
        }

        boolean xAligned = face.getXOffset() == 0;
        int fixedAdjacent = face == BlockFace.WEST || face == BlockFace.NORTH ? 15 : 0;
        int fixedCurrent = face == BlockFace.EAST || face == BlockFace.SOUTH ? 15 : 0;
        int worldBaseX = (chunkX + chunkOffsetX) << 4;
        int worldBaseZ = (chunkZ + chunkOffsetZ) << 4;

        for (int axis = 0; axis < 16; axis++) {
            for (int y = minY; y <= maxY; y++) {
                int adjacentX = xAligned ? axis : fixedAdjacent;
                int adjacentZ = xAligned ? fixedAdjacent : axis;
                int currentX = xAligned ? axis : fixedCurrent;
                int currentZ = xAligned ? fixedCurrent : axis;
                if (adjacent.getBlockState(adjacentX, y, adjacentZ) == WATER && current.getBlockState(currentX, y, currentZ) == AIR) {
                    enqueue(queue, visited, worldBaseX + adjacentX, y, worldBaseZ + adjacentZ);
                }
            }
        }
    }

    private static boolean tryFlood(Level level, Long2ObjectOpenHashMap<IChunk> chunkCache, LongArrayFIFOQueue queue, LongOpenHashSet visited,
                                    Long2ObjectOpenHashMap<BitSet> changedColumns, int x, int y, int z, int minY, int maxY) {
        if (y < minY || y > maxY) {
            return false;
        }

        IChunk targetChunk = getGeneratedChunk(level, chunkCache, x >> 4, z >> 4);
        if (targetChunk == null) {
            return false;
        }

        int localX = x & 0xF;
        int localZ = z & 0xF;
        BlockState current = targetChunk.getBlockState(localX, y, localZ);
        if (current != AIR) {
            if (current == WATER) {
                enqueue(queue, visited, x, y, z);
            }
            return false;
        }

        targetChunk.setBlockState(localX, y, localZ, WATER, 0);
        if (y >= targetChunk.getHeightMap(localX, localZ)) {
            changedColumns.computeIfAbsent(targetChunk.getIndex(), ignore -> new BitSet(256)).set((localZ << 4) | localX);
        }
        if (y > minY && targetChunk.getBlockState(localX, y - 1, localZ) == AIR) {
          //ToDo: Improve Performance -> level.scheduleUpdate(Block.get(WATER, level, x, y, z), 1);
        }
        enqueue(queue, visited, x, y, z);
        return true;
    }

    private static boolean hasHorizontalAirNeighbor(Level level, Long2ObjectOpenHashMap<IChunk> chunkCache, int x, int y, int z) {
        return isAir(level, chunkCache, x + 1, y, z)
                || isAir(level, chunkCache, x - 1, y, z)
                || isAir(level, chunkCache, x, y, z + 1)
                || isAir(level, chunkCache, x, y, z - 1);
    }

    private static boolean isAir(Level level, Long2ObjectOpenHashMap<IChunk> chunkCache, int x, int y, int z) {
        IChunk chunk = getGeneratedChunk(level, chunkCache, x >> 4, z >> 4);
        if (chunk == null) {
            return false;
        }
        return chunk.getBlockState(x & 0xF, y, z & 0xF) == AIR;
    }

    private static IChunk getGeneratedChunk(Level level, Long2ObjectOpenHashMap<IChunk> chunkCache, int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (chunkCache.containsKey(index)) {
            return chunkCache.get(index);
        }

        IChunk chunk = level.getChunk(chunkX, chunkZ, false);
        if (chunk == null || !chunk.isGenerated()) {
            chunkCache.put(index, null);
            return null;
        }

        chunkCache.put(index, chunk);
        return chunk;
    }

    private static void enqueue(LongArrayFIFOQueue queue, LongOpenHashSet visited, int x, int y, int z) {
        long key = pack(x, y, z);
        if (visited.add(key)) {
            queue.enqueue(key);
        }
    }

    private static long pack(int x, int y, int z) {
        return (((long) x & 0x3FFFFFFL) << 38)
                | (((long) z & 0x3FFFFFFL) << 12)
                | ((long) (y + 64) & 0xFFFL);
    }

    private static int unpackX(long key) {
        int x = (int) ((key >>> 38) & 0x3FFFFFFL);
        if ((x & 0x2000000) != 0) {
            x |= ~0x3FFFFFF;
        }
        return x;
    }

    private static int unpackY(long key) {
        return (int) (key & 0xFFFL) - 64;
    }

    private static int unpackZ(long key) {
        int z = (int) ((key >>> 12) & 0x3FFFFFFL);
        if ((z & 0x2000000) != 0) {
            z |= ~0x3FFFFFF;
        }
        return z;
    }

    @Override
    public String name() {
        return NAME;
    }
}
