package cn.nukkit.level.util;

import cn.nukkit.level.format.FullChunk;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public final class CachedFullChunk<T extends FullChunk> extends WeakReference<T> {
    public CachedFullChunk(T referent) {
        super(referent);
    }

    public CachedFullChunk(T referent, ReferenceQueue<? super T> q) {
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
