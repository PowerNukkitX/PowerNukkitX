package cn.nukkit.level.util;

import cn.nukkit.level.format.Chunk;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public final class CachedChunk<T extends Chunk> extends WeakReference<T> {
    public CachedChunk(T referent) {
        super(referent);
    }

    public CachedChunk(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

    /**
     * @param chunkX Chunk X
     * @param chunkZ Chunk Z
     * @return The cached chunk if the chunk is valid, otherwise null
     */
    public T validAndGet(int chunkX, int chunkZ) {
        var tmp = this.get();
        if (tmp == null || tmp.getX() != chunkX || tmp.getZ() != chunkZ) {
            return null;
        }
        return tmp;
    }
}
