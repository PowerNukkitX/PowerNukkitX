package org.powernukkitx.level.generator.object.structures.utils;

import org.powernukkitx.level.Level;
import com.google.common.base.Preconditions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Caches deterministic structure starts for the lifetime of a generator.
 *
 * @author Curse
 */
public final class StructureStartCache {

    private final ConcurrentHashMap<Class<? extends StructureStart>, ConcurrentHashMap<Long, StructureStart>> starts = new ConcurrentHashMap<>();

    /**
     * Returns the structure start for an origin chunk, creating and caching it when absent.
     */
    public <T extends StructureStart> T getOrCreate(Class<T> type, int chunkX, int chunkZ, Supplier<T> factory) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(factory);

        ConcurrentHashMap<Long, StructureStart> byChunk = this.starts.computeIfAbsent(type, ignored -> new ConcurrentHashMap<>());
        StructureStart start = byChunk.computeIfAbsent(
                Level.chunkHash(chunkX, chunkZ),
                ignored -> Preconditions.checkNotNull(factory.get())
        );
        return type.cast(start);
    }
}
