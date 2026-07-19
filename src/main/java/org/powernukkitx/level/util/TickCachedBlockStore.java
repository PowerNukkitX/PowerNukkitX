package org.powernukkitx.level.util;

import org.powernukkitx.block.Block;

/**
 * A chunk implementing this interface should hold a block cache that can be accessed concurrently; clear is usually called every tick.
 */
public interface TickCachedBlockStore {
    void clearCachedStore();

    void saveIntoCachedStore(Block block, int x, int y, int z, int layer);

    Block getFromCachedStore(int x, int y, int z, int layer);

    /**
     * Same as computeIfAbsent
     */
    Block computeFromCachedStore(int x, int y, int z, int layer, CachedBlockComputer cachedBlockComputer);

    interface CachedBlockComputer {
        Block compute();
    }
}
