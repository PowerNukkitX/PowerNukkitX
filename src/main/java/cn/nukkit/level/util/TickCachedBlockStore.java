package cn.nukkit.level.util;

import cn.nukkit.block.Block;

/**
 * 实现此接口的区块应该拥有一个能够并行访问的方块缓存，通常每tick都会调用clear。
 */
public interface TickCachedBlockStore {
    void clearCachedStore();

    void saveIntoCachedStore(Block block, int x, int y, int z, int layer);

    Block getFromCachedStore(int x, int y, int z, int layer);

    /**
     * 同computeIfAbsent
     */
    Block computeFromCachedStore(int x, int y, int z, int layer, CachedBlockComputer cachedBlockComputer);

    interface CachedBlockComputer {
        Block compute();
    }
}
