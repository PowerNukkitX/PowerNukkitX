package org.powernukkitx.level.tickingarea.storage;

import org.powernukkitx.Server;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelDBTickingAreaStorage implements TickingAreaStorage {
    private static final String PNX_TICKING_AREA_TAG = "TickingArea";
    private static final String PNX_MAX_AREAS_TAG = "MaxAreas";

    protected final Server server;
    protected final Map<UUID, TickingArea> areaMap = new HashMap<>();

    public LevelDBTickingAreaStorage(Server server) {
        this.server = server;
    }

    @Override
    public void addTickingArea(TickingArea area) {
        getStorage(area).writeTickingArea(area.getUuid(), serialize(area));
        areaMap.put(area.getUuid(), area);
    }

    @Override
    public Map<UUID, TickingArea> readTickingArea() {
        areaMap.clear();
        for (Level level : server.getLevels().values()) {
            if (!(level.getProvider() instanceof LevelDBProvider provider)) continue;

            int dimensionId = level.getDimensionData().getDimensionId();
            for (var entry : provider.getStorage().readTickingAreas().entrySet()) {
                CompoundTag tag = entry.getValue();
                if (tag.getInt("Dimension") != dimensionId) continue;

                TickingArea area = deserialize(entry.getKey(), level, tag);
                if (area != null) areaMap.put(area.getUuid(), area);
            }
        }
        return new HashMap<>(areaMap);
    }

    @Override
    public void removeTickingArea(UUID uuid) {
        TickingArea area = areaMap.remove(uuid);
        if (area != null) getStorage(area).deleteTickingArea(uuid);
    }

    @Override
    public void removeTickingArea(String name) {
        TickingArea area = areaMap.values().stream().filter(value -> value.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (area != null) removeTickingArea(area.getUuid());
    }

    @Override
    public void removeAllTickingArea() {
        for (TickingArea area : areaMap.values()) getStorage(area).deleteTickingArea(area.getUuid());
        areaMap.clear();
    }

    @Override
    public boolean containTickingArea(String name) {
        return areaMap.values().stream().anyMatch(area -> area.getName().equalsIgnoreCase(name));
    }

    @Override
    public int getMaxTickingAreas(Level level, int defaultValue) {
        CompoundTag extra = getStorage(level).readPNXExtraData();
        if (!extra.contains(PNX_TICKING_AREA_TAG)) return defaultValue;

        if (!extra.containsCompound(PNX_TICKING_AREA_TAG)) {
            throw new IllegalStateException("Invalid PNX ticking area data for world " + level.getFolderName());
        }

        CompoundTag tickingArea = extra.getCompound(PNX_TICKING_AREA_TAG);
        if (!tickingArea.contains(PNX_MAX_AREAS_TAG)) return defaultValue;

        if (!tickingArea.containsInt(PNX_MAX_AREAS_TAG)) {
            throw new IllegalStateException("Invalid PNX ticking area max for world " + level.getFolderName());
        }

        int max = tickingArea.getInt(PNX_MAX_AREAS_TAG);
        if (max < 1) {
            throw new IllegalStateException("Invalid PNX ticking area max for world " + level.getFolderName() + ": " + max);
        }

        return max;
    }

    @Override
    public void setMaxTickingAreas(Level level, int maxTickingAreas) {
        LevelDBStorage storage = getStorage(level);
        synchronized (storage) {
            CompoundTag extra = storage.readPNXExtraData();

            if (extra.contains(PNX_TICKING_AREA_TAG) && !extra.containsCompound(PNX_TICKING_AREA_TAG)) {
                throw new IllegalStateException("Invalid PNX ticking area data for world " + level.getFolderName());
            }

            CompoundTag tickingArea = extra.getCompound(PNX_TICKING_AREA_TAG);
            tickingArea.putInt(PNX_MAX_AREAS_TAG, maxTickingAreas);
            extra.putCompound(PNX_TICKING_AREA_TAG, tickingArea);
            storage.writePNXExtraData(extra);
        }
    }

    @Override
    public void resetMaxTickingAreas(Level level) {
        LevelDBStorage storage = getStorage(level);
        synchronized (storage) {
            CompoundTag extra = storage.readPNXExtraData();
            if (!extra.contains(PNX_TICKING_AREA_TAG)) return;

            if (!extra.containsCompound(PNX_TICKING_AREA_TAG)) {
                throw new IllegalStateException("Invalid PNX ticking area data for world " + level.getFolderName());
            }

            CompoundTag tickingArea = extra.getCompound(PNX_TICKING_AREA_TAG);
            tickingArea.remove(PNX_MAX_AREAS_TAG);

            if (tickingArea.isEmpty()) {
                extra.remove(PNX_TICKING_AREA_TAG);
            } else {
                extra.putCompound(PNX_TICKING_AREA_TAG, tickingArea);
            }

            storage.writePNXExtraData(extra);
        }
    }

    private LevelDBStorage getStorage(Level level) {
        if (!(level.getProvider() instanceof LevelDBProvider provider)) {
            throw new IllegalStateException("Ticking area level is not loaded from LevelDB: " + level.getName());
        }

        return provider.getStorage();
    }

    private LevelDBStorage getStorage(TickingArea area) {
        Level level = server.getLevelByName(area.getLevelName());

        if (level == null) {
            throw new IllegalStateException("Ticking area level is not loaded from LevelDB: " + area.getLevelName());
        }

        if (level.getDimensionData().getDimensionId() != area.getDimensionId()) {
            throw new IllegalStateException("Ticking area dimension mismatch: " + area.getName());
        }

        return getStorage(level);
    }

    private static TickingArea deserialize(UUID uuid, Level level, CompoundTag tag) {
        int minX = tag.getInt("MinX") >> 4;
        int minZ = tag.getInt("MinZ") >> 4;
        int maxX = tag.getInt("MaxX") >> 4;
        int maxZ = tag.getInt("MaxZ") >> 4;

        if (minX > maxX || minZ > maxZ || tag.getString("Name").length() > 255) return null;

        boolean circle = tag.getByte("IsCircle") != 0;
        int distance = circle ? (maxX - minX + 1) / 2 : 0;
        boolean preload = tag.containsByte("Preload") && tag.getByte("Preload") != 0;
        TickingArea area = new TickingArea(uuid, tag.getString("Name"), level.getName(), tag.getInt("Dimension"), circle, distance, preload);

        for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
                area.addChunk(new TickingArea.ChunkPos(chunkX, chunkZ));
            }
        }

        return area;
    }

    private static CompoundTag serialize(TickingArea area) {
        if (area.getChunks().isEmpty()) {
            throw new IllegalStateException("Cannot persist an empty ticking area: " + area.getName());
        }

        var bounds = area.minAndMaxChunkPos();
        var min = bounds.get(0);
        var max = bounds.get(1);

        CompoundTag tag = new CompoundTag()
                .putInt("Dimension", area.getDimensionId())
                .putByte("IsCircle", area.isCircle() ? 1 : 0)
                .putInt("MaxX", max.x << 4)
                .putInt("MaxZ", max.z << 4)
                .putInt("MinX", min.x << 4)
                .putInt("MinZ", min.z << 4)
                .putString("Name", area.getName());
        if (area.isPreload()) {
            tag.putByte("Preload", 1);
        }

        return tag;
    }
}
