package org.powernukkitx.level.generator.object.structures.utils;

import com.google.common.base.Preconditions;
import org.powernukkitx.level.Level;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Caches deterministic structure piece bounds for the lifetime of a generator.
 *
 * @author Curse
 */
public final class StructureBoundsCache {

    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<Long, List<BoundingBox>>> bounds = new ConcurrentHashMap<>();

    /**
     * Returns the structure piece bounds for an origin chunk, creating and caching them when absent.
     */
    public List<BoundingBox> getOrCreate(Class<?> type, int chunkX, int chunkZ, Supplier<List<BoundingBox>> factory) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(factory);

        ConcurrentHashMap<Long, List<BoundingBox>> byChunk = this.bounds.computeIfAbsent(type, ignored -> new ConcurrentHashMap<>());
        return byChunk.computeIfAbsent(
                Level.chunkHash(chunkX, chunkZ),
                ignored -> List.copyOf(Preconditions.checkNotNull(factory.get()))
        );
    }
}
