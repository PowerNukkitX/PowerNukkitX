package org.powernukkitx.level.poi;

import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * POI index of one 16x16x16 chunk section. Keyed by section-relative position,
 * with a per-type inverted index for fast type queries. Instances are read from
 * AI worker threads and written from the main thread, hence the coarse lock.
 * <p>
 * (including ticket moves); {@code valid} is false when the stored data cannot be
 * trusted (format change) and the section must be rebuilt from the blocks.
 */
public final class PoiSection {

    private static final int FORMAT_VERSION = 1;

    private final Short2ObjectOpenHashMap<PoiRecord> records = new Short2ObjectOpenHashMap<>();
    private final Map<PoiType, Set<PoiRecord>> byType = new HashMap<>();
    private final Runnable markDirty = () -> this.dirty = true;
    private volatile boolean dirty;
    private volatile boolean valid;

    public PoiSection() {
        this(true);
    }

    private PoiSection(boolean valid) {
        this.valid = valid;
    }

    private static short key(int localX, int localY, int localZ) {
        return (short) ((localX << 8) | (localZ << 4) | localY);
    }

    public synchronized void add(BlockVector3 pos, PoiType type) {
        short key = key(pos.x & 0xF, pos.y & 0xF, pos.z & 0xF);
        PoiRecord previous = records.get(key);
        if (previous != null) {
            if (previous.getType() == type) {
                return;
            }
            removeRecord(key, previous);
        }
        putRecord(key, new PoiRecord(pos, type, markDirty));
        this.dirty = true;
    }

    /**
     * Inserts a record with a known ticket count (deserialization / consistency refresh).
     */
    public synchronized void addLoaded(BlockVector3 pos, PoiType type, int freeTickets) {
        putRecord(key(pos.x & 0xF, pos.y & 0xF, pos.z & 0xF), new PoiRecord(pos, type, freeTickets, markDirty));
    }

    private void putRecord(short key, PoiRecord record) {
        records.put(key, record);
        byType.computeIfAbsent(record.getType(), t -> new HashSet<>()).add(record);
    }

    public synchronized void remove(int localX, int localY, int localZ) {
        short key = key(localX, localY, localZ);
        PoiRecord record = records.get(key);
        if (record != null) {
            removeRecord(key, record);
            this.dirty = true;
        }
    }

    private void removeRecord(short key, PoiRecord record) {
        records.remove(key);
        Set<PoiRecord> ofType = byType.get(record.getType());
        if (ofType != null) {
            ofType.remove(record);
            if (ofType.isEmpty()) {
                byType.remove(record.getType());
            }
        }
    }

    public synchronized @Nullable PoiRecord get(int localX, int localY, int localZ) {
        return records.get(key(localX, localY, localZ));
    }

    public synchronized void collect(Predicate<PoiType> typePredicate, Predicate<PoiRecord> occupancy, List<PoiRecord> out) {
        for (Map.Entry<PoiType, Set<PoiRecord>> entry : byType.entrySet()) {
            if (typePredicate.test(entry.getKey())) {
                for (PoiRecord record : entry.getValue()) {
                    if (occupancy.test(record)) {
                        out.add(record);
                    }
                }
            }
        }
    }

    public synchronized List<PoiRecord> allRecords() {
        return new ArrayList<>(records.values());
    }

    public synchronized void clearRecords() {
        records.clear();
        byType.clear();
        this.dirty = true;
    }

    public synchronized boolean isEmpty() {
        return records.isEmpty();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Stream<? extends PoiRecord> getRecords(Predicate<PoiType> typePredicate, PoiManager.Occupancy occupancy) {
        return byType.entrySet().stream().filter(entry -> typePredicate.test(entry.getKey())).flatMap(entry -> entry.getValue().stream()).filter(occupancy.getTest());
    }
}
