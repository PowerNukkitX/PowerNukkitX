package org.powernukkitx.level.format;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockLightProperties;
import org.powernukkitx.block.BlockState;

/**
 * Internal heightmap maintenance for Chunk/UnsafeChunk.
 *
 * <p>Runtime and post-terrain block changes are handled incrementally. Full recalculation remains
 * available for chunk finalization/loading.
 *
 * @author Curse
 */
final class ChunkHeightMap {
    private static final int HEIGHT = 1;
    private static final int RENDER_HEIGHT = 1 << 1;
    private static final int BOTH = HEIGHT | RENDER_HEIGHT;
    private ChunkHeightMap() {}

    static int cellMask(ChunkSection section, int x, int localY, int z) {
        return cellMask(section.getBlockState(x, localY, z, 0), section.getBlockState(x, localY, z, 1));
    }

    static int cellMaskWithState(ChunkSection section, int x, int localY, int z, int layer, BlockState state) {
        BlockState layer0 = layer == 0 ? state : section.getBlockState(x, localY, z, 0);
        BlockState layer1 = layer == 1 ? state : section.getBlockState(x, localY, z, 1);
        return cellMask(layer0, layer1);
    }

    static long cellMasksWithStateChange(ChunkSection section, int x, int localY, int z, int layer, BlockState oldState, BlockState newState) {
        int oldMask;
        int newMask;

        if (layer == 0) {
            int secondaryMask = stateMask(section.getBlockState(x, localY, z, 1), false);
            int oldPrimaryMask = stateMask(oldState, true);
            int newPrimaryMask = stateMask(newState, true);
            oldMask = oldPrimaryMask == BOTH ? BOTH : oldPrimaryMask | secondaryMask;
            newMask = newPrimaryMask == BOTH ? BOTH : newPrimaryMask | secondaryMask;
        } else {
            int primaryMask = stateMask(section.getBlockState(x, localY, z, 0), true);
            if (primaryMask == BOTH) {
                oldMask = BOTH;
                newMask = BOTH;
            } else {
                oldMask = primaryMask | stateMask(oldState, false);
                newMask = primaryMask | stateMask(newState, false);
            }
        }

        return ((long) oldMask << 32) | (newMask & 0xffffffffL);
    }

    static void updateAfterMaskChange(Chunk chunk, int x, int y, int z, int oldMask, int newMask) {
        int changedMask = oldMask ^ newMask;
        if (changedMask == 0) return;

        int minHeight = chunk.getDimensionData().getMinHeight();
        int columnIndex = (z << 4) | x;
        int oldHeight = chunk.heightMap[columnIndex] + minHeight;
        int oldRenderHeight = chunk.renderHeightMap[columnIndex] + minHeight;
        int newHeight = oldHeight;
        int newRenderHeight = oldRenderHeight;
        int searchMask = 0;
        /*
         * Normal heightmap.
         *
         * Adding a blocker can only move the height upward and therefore is
         * O(1).
         *
         * Removing a block matters only when it was the current highest
         * blocker. Only that case requires a downward search.
         */
        if ((changedMask & HEIGHT) != 0) {
            if ((newMask & HEIGHT) != 0) {
                if (y >= oldHeight) newHeight = y + 1;
            } else if (y == oldHeight - 1) {
                searchMask |= HEIGHT;
            }
        }

        /*
         * Render heightmap.
         */
        if ((changedMask & RENDER_HEIGHT) != 0) {
            if ((newMask & RENDER_HEIGHT) != 0) {
                if (y >= oldRenderHeight) newRenderHeight = y + 1;
            } else if (y == oldRenderHeight - 1) {
                searchMask |= RENDER_HEIGHT;
            }
        }

        /*
         * If a current top blocker disappeared, search only below the
         * changed Y. The search skips absent/empty SubChunks entirely and can
         * resolve both maps in the same pass.
         */
        if (searchMask != 0) {
            long found = findBelow(chunk, x, z, y - 1, searchMask);
            if ((searchMask & HEIGHT) != 0) {
                newHeight = foundHeight(found);
            }

            if ((searchMask & RENDER_HEIGHT) != 0) {
                newRenderHeight = foundRenderHeight(found);
            }
        }

        if (newHeight != oldHeight) {
            chunk.heightMap[columnIndex] = (short) (newHeight - minHeight);
        }

        if (newRenderHeight != oldRenderHeight) {
            chunk.renderHeightMap[columnIndex] = (short) (newRenderHeight - minHeight);
        }
    }

    static int recalculateColumn(Chunk chunk, int x, int z) {
        int minHeight = chunk.getDimensionData().getMinHeight();
        long found = findBelow(chunk, x, z, chunk.getDimensionData().getMaxHeight(), BOTH);
        int height = foundHeight(found);
        int renderHeight = foundRenderHeight(found);
        int columnIndex = (z << 4) | x;
        chunk.heightMap[columnIndex] = (short) (height - minHeight);
        chunk.renderHeightMap[columnIndex] = (short) (renderHeight - minHeight);
        return height;
    }

    static void recalculateAll(Chunk chunk) {
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                recalculateColumn(chunk, x, z);
            }
        }
    }

    static void recalculateRender(Chunk chunk) {
        int minHeight = chunk.getDimensionData().getMinHeight();
        int maxHeight = chunk.getDimensionData().getMaxHeight();
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                long found = findBelow(chunk, x, z, maxHeight, RENDER_HEIGHT);
                chunk.renderHeightMap[(z << 4) | x] = (short) (foundRenderHeight(found) - minHeight);
            }
        }
    }

    private static long findBelow(Chunk chunk, int x, int z, int startY, int requestedMask) {
        int minHeight = chunk.getDimensionData().getMinHeight();
        int maxHeight = chunk.getDimensionData().getMaxHeight();
        int minSectionY = chunk.getDimensionData().getMinSectionY();
        int height = minHeight;
        int renderHeight = minHeight;
        int pendingMask = requestedMask;
        int y = Math.min(startY, maxHeight);
        while (y >= minHeight && pendingMask != 0) {
            int sectionY = y >> 4;
            int sectionIndex = sectionY - minSectionY;
            int sectionMinY = Math.max(minHeight, sectionY << 4);
            if (sectionIndex < 0 || sectionIndex >= chunk.sections.length) {
                y = sectionMinY - 1;
                continue;
            }

            ChunkSection section = chunk.sections[sectionIndex];
            /*
             * A null or completely empty SubChunk cannot contain either
             * heightmap blocker. Skip all 16 Y values at once.
             */
            if (section != null && !section.isEmpty()) {
                for (int blockY = y; blockY >= sectionMinY; blockY--) {
                    int mask = cellMask(section, x, blockY & 0x0f, z);
                    if ((pendingMask & HEIGHT) != 0 && (mask & HEIGHT) != 0) {
                        height = blockY + 1;
                        pendingMask &= ~HEIGHT;
                    }

                    if ((pendingMask & RENDER_HEIGHT) != 0 && (mask & RENDER_HEIGHT) != 0) {
                        renderHeight = blockY + 1;
                        pendingMask &= ~RENDER_HEIGHT;
                    }

                    if (pendingMask == 0) {
                        break;
                    }
                }
            }

            y = sectionMinY - 1;
        }

        return pack(height, renderHeight);
    }

    private static int cellMask(BlockState layer0, BlockState layer1) {
        int mask = stateMask(layer0, true);
        if (mask != BOTH) {
            mask |= stateMask(layer1, false);
        }

        return mask;
    }

    private static int stateMask(BlockState state, boolean primaryLayer) {
        if (state == BlockAir.STATE) return 0;

        int packed = BlockLightProperties.packed(state);
        if (!BlockLightProperties.contributesToHeightMap(packed, primaryLayer)) return 0;

        /*
         * Water contributes to the normal heightmap but not to the render
         * heightmap, matching the previous UnsafeChunk implementation.
         */
        return BlockLightProperties.isWater(packed) ? HEIGHT : BOTH;
    }

    private static long pack(int height, int renderHeight) {
        return ((long) height << 32) | (renderHeight & 0xffffffffL);
    }

    private static int foundHeight(long result) {
        return (int) (result >> 32);
    }

    private static int foundRenderHeight(long result) {
        return (int) result;
    }
}
