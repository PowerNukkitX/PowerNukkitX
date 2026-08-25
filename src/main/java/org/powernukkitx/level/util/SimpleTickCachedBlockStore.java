package org.powernukkitx.level.util;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.Level;
import org.powernukkitx.utils.collection.nb.Int2ObjectNonBlockingMap;


public final class SimpleTickCachedBlockStore implements TickCachedBlockStore {
    private final Int2ObjectNonBlockingMap<Block> tickCachedBlockStore;
    private final Level level;

    public SimpleTickCachedBlockStore(Level level) {
        this.tickCachedBlockStore = new Int2ObjectNonBlockingMap<>(64);
        this.level = level;
    }

    @Override
    public void clearCachedStore() {
        if (!this.tickCachedBlockStore.isEmpty()) {
            this.tickCachedBlockStore.clear();
        }
    }

    @Override
    public boolean isCachedStoreEmpty() {
        return this.tickCachedBlockStore.isEmpty();
    }

    @Override
    public void saveIntoCachedStore(Block block, int x, int y, int z, int layer) {
        tickCachedBlockStore.put(Level.localBlockHash(x, y, z, layer, level), block);
    }

    @Override
    public Block getFromCachedStore(int x, int y, int z, int layer) {
        return tickCachedBlockStore.get(Level.localBlockHash(x, y, z, layer, level));
    }

    @Override
    public Block putIfAbsentInCachedStore(Block block, int x, int y, int z, int layer) {
        Block previous = tickCachedBlockStore.putIfAbsent(Level.localBlockHash(x, y, z, layer, level), block);
        return previous != null ? previous : block;
    }

    @Override
    public Block computeFromCachedStore(int x, int y, int z, int layer, CachedBlockComputer cachedBlockComputer) {
        int key = Level.localBlockHash(x, y, z, layer, level);
        Block cached = tickCachedBlockStore.get(key);
        if (cached != null) return cached;
        Block computed = cachedBlockComputer.compute();
        if (computed == null) return null;
        Block previous = tickCachedBlockStore.putIfAbsent(key, computed);
        return previous != null ? previous : computed;
    }
}
