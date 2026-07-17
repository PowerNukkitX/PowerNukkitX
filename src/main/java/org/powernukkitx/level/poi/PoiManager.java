package org.powernukkitx.level.poi;

import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Per-level point-of-interest index (vanilla {@code PoiManager} transposed).
 * <p>
 * The world is never scanned on demand: {@link Level#setBlock} reports every block
 * change through {@link #onBlockChange}, and a chunk section is scanned at most once,
 * lazily, the first time a query touches it (with a palette pre-check so sections
 * without any POI block are skipped for free). Sections are dropped on chunk unload.
 * <p>
 * The index is in-memory only: after a chunk reload, tickets are re-acquired by the
 * entities when their memories are validated.
 */
public class PoiManager {

    public enum Occupancy {
        HAS_SPACE(PoiRecord::hasSpace),
        IS_OCCUPIED(PoiRecord::isOccupied),
        ANY(record -> true);

        private final Predicate<PoiRecord> test;

        Occupancy(Predicate<PoiRecord> test) {
            this.test = test;
        }

        public Predicate<PoiRecord> getTest() {
            return test;
        }
    }

    private static final String EXTRA_DATA_KEY = "PNXPoi";

    private final Level level;
    private final ConcurrentHashMap<Long, PoiSection> sections = new ConcurrentHashMap<>();

    public PoiManager(Level level) {
        this.level = level;
    }

    private static long sectionKey(int chunkX, int sectionY, int chunkZ) {
        return ((chunkX & 0xFFFFFFFL) << 36) | ((chunkZ & 0xFFFFFFFL) << 8) | (sectionY & 0xFFL);
    }

    /**
     * Called by {@link Level#setBlock} for every layer-0 block change. Only sections
     * already indexed are patched; unindexed sections will see the new state when
     * they are lazily scanned.
     */
    public void onBlockChange(int x, int y, int z, BlockState oldState, BlockState newState) {
        PoiType oldType = PoiTypeRegistry.forState(oldState);
        PoiType newType = PoiTypeRegistry.forState(newState);
        if (oldType == newType) {
            return;
        }
        PoiSection section = sections.get(sectionKey(x >> 4, y >> 4, z >> 4));
        if (section == null) {
            return;
        }
        if (oldType != null) {
            section.remove(x & 0xF, y & 0xF, z & 0xF);
        }
        if (newType != null) {
            section.add(new BlockVector3(x, y, z), newType);
        }
    }

    public void onChunkUnload(int chunkX, int chunkZ) {
        DimensionData dimension = level.getDimensionData();
        for (int sectionY = dimension.getMinSectionY(); sectionY <= dimension.getMaxSectionY(); sectionY++) {
            sections.remove(sectionKey(chunkX, sectionY, chunkZ));
        }
    }

    public Optional<PoiRecord> findClosest(Predicate<PoiType> typePredicate, Vector3 center, int radius, Occupancy occupancy) {
        return getInRange(typePredicate, center, radius, occupancy).stream()
                .min(Comparator.comparingDouble(record -> distanceSquared(record.getPos(), center)));
    }

    public List<PoiRecord> getInRange(Predicate<PoiType> typePredicate, Vector3 center, int radius, Occupancy occupancy) {
        List<PoiRecord> result = new ArrayList<>();
        DimensionData dimension = level.getDimensionData();
        int centerX = center.getFloorX();
        int centerY = center.getFloorY();
        int centerZ = center.getFloorZ();
        int chunkRadius = (radius >> 4) + 1;
        int centerChunkX = centerX >> 4;
        int centerChunkZ = centerZ >> 4;
        int minSectionY = Math.max(dimension.getMinSectionY(), (centerY - radius) >> 4);
        int maxSectionY = Math.min(dimension.getMaxSectionY(), (centerY + radius) >> 4);

        List<PoiRecord> candidates = new ArrayList<>();
        for (int chunkX = centerChunkX - chunkRadius; chunkX <= centerChunkX + chunkRadius; chunkX++) {
            for (int chunkZ = centerChunkZ - chunkRadius; chunkZ <= centerChunkZ + chunkRadius; chunkZ++) {
                for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
                    PoiSection section = getOrScan(chunkX, sectionY, chunkZ);
                    if (section == null || section.isEmpty()) {
                        continue;
                    }
                    section.collect(typePredicate, occupancy.getTest(), candidates);
                }
            }
        }
        for (PoiRecord record : candidates) {
            BlockVector3 pos = record.getPos();
            if (Math.abs(pos.x - centerX) <= radius && Math.abs(pos.y - centerY) <= radius && Math.abs(pos.z - centerZ) <= radius) {
                result.add(record);
            }
        }
        return result;
    }

    public Optional<PoiType> getType(BlockVector3 pos) {
        PoiRecord record = getRecord(pos);
        return record == null ? Optional.empty() : Optional.of(record.getType());
    }

    /**
     * Claims a ticket on the POI at the given position. Returns true when the ticket
     * was acquired, or when the position is not indexed (unloaded chunk / index lag) so
     * callers never get blocked by a stale index.
     */
    public boolean takeAt(BlockVector3 pos) {
        PoiSection section = getOrScan(pos.x >> 4, pos.y >> 4, pos.z >> 4);
        if (section == null) {
            return true;
        }
        PoiRecord record = section.get(pos.x & 0xF, pos.y & 0xF, pos.z & 0xF);
        if (record == null) {
            return true;
        }
        return record.acquireTicket();
    }

    public void release(BlockVector3 pos) {
        PoiSection section = sections.get(sectionKey(pos.x >> 4, pos.y >> 4, pos.z >> 4));
        if (section != null) {
            PoiRecord record = section.get(pos.x & 0xF, pos.y & 0xF, pos.z & 0xF);
            if (record != null) {
                record.releaseTicket();
            }
        }
    }

    private @Nullable PoiRecord getRecord(BlockVector3 pos) {
        PoiSection section = getOrScan(pos.x >> 4, pos.y >> 4, pos.z >> 4);
        return section == null ? null : section.get(pos.x & 0xF, pos.y & 0xF, pos.z & 0xF);
    }

    /**
     * Writes the dirty sections of a chunk into its extra data compound so they are
     * persisted with the chunk (called from the chunk serializer). Sections never
     * loaded into the manager keep their previously stored data untouched.
     */
    public void flushToChunk(int chunkX, int chunkZ, @Nullable CompoundTag extraData) {
        if (extraData == null) {
            return;
        }
        DimensionData dimension = level.getDimensionData();
        CompoundTag poiTag = extraData.contains(EXTRA_DATA_KEY) ? extraData.getCompound(EXTRA_DATA_KEY) : new CompoundTag();
        boolean changed = false;
        for (int sectionY = dimension.getMinSectionY(); sectionY <= dimension.getMaxSectionY(); sectionY++) {
            PoiSection section = sections.get(sectionKey(chunkX, sectionY, chunkZ));
            if (section == null || !section.isDirty()) {
                continue;
            }
            section.clearDirty();
            String key = "s" + sectionY;
            if (section.isEmpty()) {
                poiTag.remove(key);
            } else {
                poiTag.putCompound(key, section.pack());
            }
            changed = true;
        }
        if (changed) {
            if (poiTag.getTags().isEmpty()) {
                extraData.remove(EXTRA_DATA_KEY);
            } else {
                extraData.putCompound(EXTRA_DATA_KEY, poiTag);
            }
        }
    }

    /**
     * Idempotent claim used to re-validate an entity memory (e.g. after a chunk reload).
     */
    public boolean ensureTicket(BlockVector3 pos) {
        PoiRecord record = getRecord(pos);
        return record == null || record.ensureTicket();
    }

    private @Nullable PoiSection getOrScan(int chunkX, int sectionY, int chunkZ) {
        long key = sectionKey(chunkX, sectionY, chunkZ);
        PoiSection existing = sections.get(key);
        if (existing != null) {
            return existing;
        }
        IChunk chunk = level.getChunkIfLoaded(chunkX, chunkZ);
        if (chunk == null) {
            return null;
        }
        return sections.computeIfAbsent(key, k -> loadSection(chunk, chunkX, sectionY, chunkZ));
    }

    private PoiSection loadSection(IChunk chunk, int chunkX, int sectionY, int chunkZ) {
        CompoundTag extraData = chunk.getExtraData();
        if (extraData != null && extraData.contains(EXTRA_DATA_KEY)) {
            CompoundTag poiTag = extraData.getCompound(EXTRA_DATA_KEY);
            String key = "s" + sectionY;
            if (poiTag.contains(key)) {
                PoiSection section = PoiSection.unpack(poiTag.getCompound(key));
                if (!section.isValid()) {
                    refresh(chunk, chunkX, sectionY, chunkZ, section);
                }
                return section;
            }
        }
        return scan(chunk, chunkX, sectionY, chunkZ);
    }

    /**
     * Rebuilds an untrusted section from the actual blocks, carrying over the ticket
     * counts of records that still match (vanilla {@code PoiSection.refresh}).
     */
    private void refresh(IChunk chunk, int chunkX, int sectionY, int chunkZ, PoiSection section) {
        Map<BlockVector3, PoiRecord> old = new HashMap<>();
        for (PoiRecord record : section.allRecords()) {
            old.put(record.getPos(), record);
        }
        section.clearRecords();
        PoiSection scanned = scan(chunk, chunkX, sectionY, chunkZ);
        for (PoiRecord record : scanned.allRecords()) {
            PoiRecord previous = old.get(record.getPos());
            if (previous != null && previous.getType() == record.getType()) {
                section.addLoaded(record.getPos(), record.getType(), previous.getFreeTickets());
            } else {
                section.add(record.getPos(), record.getType());
            }
        }
        section.setValid(true);
    }

    private PoiSection scan(IChunk chunk, int chunkX, int sectionY, int chunkZ) {
        PoiSection section = new PoiSection();
        try {
            ChunkSection chunkSection = chunk.getSection(sectionY);
            if (chunkSection == null || chunkSection.isEmpty()) {
                return section;
            }
            if (!chunkSection.blockLayer()[0].anyMatch(state -> PoiTypeRegistry.isPoiBlock(state.getIdentifier()))) {
                return section;
            }
            int baseX = chunkX << 4;
            int baseY = sectionY << 4;
            int baseZ = chunkZ << 4;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        BlockState state = chunkSection.getBlockState(x, y, z, 0);
                        PoiType type = PoiTypeRegistry.forState(state);
                        if (type != null) {
                            section.add(new BlockVector3(baseX + x, baseY + y, baseZ + z), type);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // concurrent palette mutation during the scan: keep what was indexed so far,
            // onBlockChange keeps the section consistent from here on
        }
        return section;
    }

    private static double distanceSquared(BlockVector3 pos, Vector3 center) {
        double dx = pos.x + 0.5 - center.x;
        double dy = pos.y + 0.5 - center.y;
        double dz = pos.z + 0.5 - center.z;
        return dx * dx + dy * dy + dz * dz;
    }
}
