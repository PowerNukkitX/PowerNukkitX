package org.powernukkitx.level.util;

import org.powernukkitx.block.Block;

/**
 * A chunk implementing this interface should hold a block cache that can be accessed concurrently; clear is usually called every tick.
 */
public interface TickCachedBlockStore {
    void clearCachedStore();

    default boolean isCachedStoreEmpty() {
        return false;
    }

    void saveIntoCachedStore(Block block, int x, int y, int z, int layer);

    Block getFromCachedStore(int x, int y, int z, int layer);

    /**
     * Same as computeIfAbsent
     */
    Block computeFromCachedStore(int x, int y, int z, int layer, CachedBlockComputer cachedBlockComputer);

    /**
     * Caches {@code block} only if the position has no entry yet, and returns whichever instance
     * ended up in the cache. This preserves the "one Block object per position per tick" guarantee
     * that {@link #computeFromCachedStore} provides, without forcing the caller to allocate a
     * capturing {@link CachedBlockComputer} on a path where the value is already computed.
     *
     * <p>This default is a non-atomic get-then-save; two racing callers may each keep their own
     * instance. Implementations backed by a concurrent map should override it with a real
     * {@code putIfAbsent}.
     */
    default Block putIfAbsentInCachedStore(Block block, int x, int y, int z, int layer) {
        Block existing = getFromCachedStore(x, y, z, layer);
        if (existing != null) return existing;
        saveIntoCachedStore(block, x, y, z, layer);
        return block;
    }

    interface CachedBlockComputer {
        Block compute();
    }
}
