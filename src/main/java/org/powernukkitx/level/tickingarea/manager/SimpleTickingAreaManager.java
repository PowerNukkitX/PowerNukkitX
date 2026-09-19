package org.powernukkitx.level.tickingarea.manager;

import com.google.common.base.Preconditions;
import org.powernukkitx.Server;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.powernukkitx.level.tickingarea.storage.TickingAreaStorage;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SimpleTickingAreaManager extends TickingAreaManager {
    protected Map<UUID, TickingArea> areaMap;

    public SimpleTickingAreaManager(TickingAreaStorage storage) {
        super(storage);
        areaMap = new HashMap<>();
    }

    @Override
    public void addTickingArea(TickingArea area) {
        Level level = Server.getInstance().getLevelByName(area.getLevelName());
        Preconditions.checkState(level != null, "Ticking area level is not loaded: %s", area.getLevelName());
        Preconditions.checkState(canAddTickingArea(level), "Maximum ticking area count reached for world %s: %s", level.getFolderName(), getMaxTickingAreas(level));
        Preconditions.checkState(area.loadAllChunk(), "Failed to load ticking area chunks: %s", area.getName());
        storage.addTickingArea(area);
        areaMap.put(area.getUuid(), area);
        bumpVersion();
    }

    @Override
    public void removeTickingArea(TickingArea area) {
        if (areaMap.remove(area.getUuid()) == null) return;
        storage.removeTickingArea(area.getUuid());
        bumpVersion();
    }

    @Override
    public void removeTickingArea(String name) {
        TickingArea area = getTickingArea(name);
        if (area != null) removeTickingArea(area);
    }

    @Override
    public void removeAllTickingArea() {
        storage.removeAllTickingArea();
        areaMap.clear();
        bumpVersion();
    }

    @Override
    public void removeAllTickingArea(Level level) {
        Set<TickingArea> areas = getTickingAreas(level);
        if (areas.isEmpty()) return;
        for (TickingArea area : areas) {
            storage.removeTickingArea(area.getUuid());
            areaMap.remove(area.getUuid());
        }
        bumpVersion();
    }

    @Override
    public @Nullable TickingArea getTickingArea(String name) {
        return areaMap.values().stream().filter(area -> area.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public @Nullable TickingArea getTickingArea(Level level, String name) {
        return areaMap.values().stream()
                .filter(area -> area.getLevelName().equals(level.getName()) && area.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    @Override
    public boolean containTickingArea(String name) {
        return getTickingArea(name) != null;
    }

    @Override
    public boolean containTickingArea(Level level, String name) {
        return getTickingArea(level, name) != null;
    }

    @Override
    public Set<TickingArea> getAllTickingArea() {
        return new HashSet<>(areaMap.values());
    }

    @Override
    public Set<TickingArea> getTickingAreas(Level level) {
        Set<TickingArea> areas = new HashSet<>();
        for (TickingArea area : areaMap.values()) {
            if (area.getLevelName().equals(level.getName())) areas.add(area);
        }
        return areas;
    }

    @Override
    public Set<TickingArea> getTickingAreasInWorld(Level level) {
        Set<TickingArea> areas = new HashSet<>();
        String folderName = level.getFolderName();
        for (TickingArea area : areaMap.values()) {
            Level areaLevel = Server.getInstance().getLevelByName(area.getLevelName());
            if (areaLevel != null && areaLevel.getFolderName().equals(folderName)) areas.add(area);
        }
        return areas;
    }

    @Override
    public int getTickingAreaCount(Level level) {
        int count = 0;
        for (TickingArea area : areaMap.values()) {
            if (area.getLevelName().equals(level.getName())) count++;
        }
        return count;
    }

    @Override
    public boolean hasAreas() {
        return !areaMap.isEmpty();
    }

    @Override
    public @Nullable TickingArea getTickingAreaByChunk(String levelName, TickingArea.ChunkPos chunkPos) {
        TickingArea matchedArea = null;
        for (var area : areaMap.values()) {
            boolean matched = area.getLevelName().equals(levelName) && area.getChunks().stream().anyMatch(pos -> pos.equals(chunkPos));
            if (matched) {
                matchedArea = area;
                break;
            }
        }
        return matchedArea;
    }

    @Override
    public TickingArea getTickingAreaByPos(Position pos) {
        return getTickingAreaByChunk(pos.getLevelName(), new TickingArea.ChunkPos(pos.getChunkX(), pos.getChunkZ()));
    }

    @Override
    public Set<TickingArea> getTickingAreasByPos(Position pos) {
        Set<TickingArea> areas = new HashSet<>();
        TickingArea.ChunkPos chunkPos = new TickingArea.ChunkPos(pos.getChunkX(), pos.getChunkZ());
        for (TickingArea area : areaMap.values()) {
            if (area.getLevelName().equals(pos.getLevelName()) && area.getChunks().contains(chunkPos)) areas.add(area);
        }
        return areas;
    }

    @Override
    public void setTickingAreaPreload(TickingArea area, boolean preload) {
        TickingArea current = areaMap.get(area.getUuid());
        if (current == null || current.isPreload() == preload) return;
        TickingArea updated = new TickingArea(current.getUuid(), current.getName(), current.getLevelName(), current.getDimensionId(),
                current.isCircle(), current.getDistance(), preload, current.getChunks().toArray(TickingArea.ChunkPos[]::new));
        storage.addTickingArea(updated);
        areaMap.put(updated.getUuid(), updated);
    }

    @Override
    public void loadAllTickingArea() {
        areaMap.clear();
        areaMap.putAll(storage.readTickingArea());
        bumpVersion();
        for (TickingArea area : areaMap.values()) {
            if (area.isPreload()) Preconditions.checkState(area.loadAllChunk(), "Failed to preload ticking area chunks: %s", area.getName());
        }
        for (TickingArea area : areaMap.values()) {
            if (!area.isPreload()) Preconditions.checkState(area.loadAllChunk(), "Failed to load ticking area chunks: %s", area.getName());
        }
    }
}
