package cn.nukkit.level.util;

import cn.nukkit.block.Block;
import cn.nukkit.level.Dimension;

import java.util.concurrent.ConcurrentHashMap;


public final class SimpleTickCachedBlockStore implements TickCachedBlockStore {
    private final ConcurrentHashMap<Integer, Block> tickCachedBlockStore;
    private final Dimension level;

    public SimpleTickCachedBlockStore(Dimension level) {
        this.tickCachedBlockStore = new ConcurrentHashMap<>(32, 0.75f);
        this.level = level;
    }

    @Override
    public void clearCachedStore() {
        this.tickCachedBlockStore.clear();
    }

    @Override
    public void saveIntoCachedStore(Block block, int x, int y, int z, int layer) {
        tickCachedBlockStore.put(Dimension.localBlockHash(x, y, z, layer, level), block);
    }

    @Override
    public Block getFromCachedStore(int x, int y, int z, int layer) {
        return tickCachedBlockStore.get(Dimension.localBlockHash(x, y, z, layer, level));
    }

    @Override
    public Block computeFromCachedStore(int x, int y, int z, int layer, CachedBlockComputer cachedBlockComputer) {
        return tickCachedBlockStore.computeIfAbsent(Dimension.localBlockHash(x, y, z, layer, level), ignore -> cachedBlockComputer.compute());
    }
}
