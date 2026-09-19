package org.powernukkitx.level.format;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.PrecipitationBehavior;
import org.powernukkitx.math.BlockFace;

import java.util.Arrays;

/**
 * Internal precipitation height cache for Chunk/UnsafeChunk.
 *
 * @author Curse
 */
final class ChunkRainHeightMap {
    private static final short UNCALCULATED = -999;
    private static final short EMPTY_COLUMN = -1;

    private ChunkRainHeightMap() {
    }

    static short[] create() {
        short[] rainHeightMap = new short[256];
        Arrays.fill(rainHeightMap, UNCALCULATED);
        return rainHeightMap;
    }

    static int get(Chunk chunk, int x, int z) {
        int index = (z << 4) | x;
        int minHeight = chunk.getDimensionData().getMinHeight();
        short cached = chunk.rainHeightMap[index];

        if (cached != UNCALCULATED) {
            return minHeight + cached;
        }

        int minSectionY = chunk.getDimensionData().getMinSectionY();
        for (int y = chunk.getDimensionData().getMaxHeight(); y >= minHeight; y--) {
            int sectionIndex = (y >> 4) - minSectionY;
            if (sectionIndex < 0 || sectionIndex >= chunk.sections.length) {
                continue;
            }

            ChunkSection section = chunk.sections[sectionIndex];
            if (section == null) {
                continue;
            }

            int localY = y & 0x0f;
            if (obstructsRain(section.getBlockState(x, localY, z, 0))
                    || obstructsRain(section.getBlockState(x, localY, z, 1))) {
                short height = (short) (y - minHeight + 1);
                chunk.rainHeightMap[index] = height;
                return minHeight + height;
            }
        }

        chunk.rainHeightMap[index] = EMPTY_COLUMN;
        return minHeight - 1;
    }

    static void invalidateAfterBlockChange(Chunk chunk, int x, int y, int z) {
        int index = (z << 4) | x;
        short cached = chunk.rainHeightMap[index];
        if (cached == UNCALCULATED) {
            return;
        }

        int localY = y - chunk.getDimensionData().getMinHeight();
        if (localY >= cached - 1) {
            chunk.rainHeightMap[index] = UNCALCULATED;
        }
    }

    static void invalidateAll(Chunk chunk) {
        Arrays.fill(chunk.rainHeightMap, UNCALCULATED);
    }

    private static boolean obstructsRain(BlockState state) {
        if (state == BlockAir.STATE) {
            return false;
        }

        Block block = state.toBlock();
        PrecipitationBehavior behavior = block.getPrecipitationBehavior();
        return behavior != null ? behavior.obstructsRain() : block.isSolid(BlockFace.UP);
    }
}
