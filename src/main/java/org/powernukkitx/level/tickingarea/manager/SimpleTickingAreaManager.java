package org.powernukkitx.level.tickingarea.manager;

import com.google.common.base.Preconditions;
import org.powernukkitx.Server;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.powernukkitx.level.tickingarea.storage.TickingAreaStorage;

import javax.annotation.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTickingAreaManager extends TickingAreaManager {
    protected Map<UUID, TickingArea> areaMap;

    public SimpleTickingAreaManager(TickingAreaStorage storage) {
        super(storage);
        areaMap = new ConcurrentHashMap<>();
    }

    @Override
    public void addTickingArea(TickingArea area) {
        Level level = Server.getInstance().getLevelByName(area.getLevelName());
        Preconditions.checkState(level != null, "Ticking area level is not loaded: %s", area.getLevelName());
        Preconditions.checkState(
                level.getDimensionData().getDimensionId() == area.getDimensionId(),
                "Ticking area dimension does not match level %s", area.getLevelName());
        Preconditions.checkState(canAddTickingArea(level), "Maximum ticking area count reached for world %s: %s",
                level.getFolderName(), getMaxTickingAreas(level));

        TickingArea previous = areaMap.get(area.getUuid());
        level.updateTickingAreaChunkView(area);
        areaMap.put(area.getUuid(), area);
        bumpVersion();

        try {
            Preconditions.checkState(area.loadAllChunk(), "Failed to load ticking area chunks: %s", area.getName());
            storage.addTickingArea(area);

            if (previous != null
                    && (!previous.getLevelName().equals(area.getLevelName())
                    || previous.getDimensionId() != area.getDimensionId())) {
                releaseChunkView(previous);
            }
        } catch (RuntimeException | Error throwable) {
            if (previous == null) {
                areaMap.remove(area.getUuid(), area);
                level.removeTickingAreaChunkView(area.getUuid());
            } else {
                areaMap.put(area.getUuid(), previous);
                if (previous.getLevelName().equals(area.getLevelName())
                        && previous.getDimensionId() == area.getDimensionId()) {
                    level.updateTickingAreaChunkView(previous);
                } else {
                    level.removeTickingAreaChunkView(area.getUuid());
                }
            }
            bumpVersion();
            throw throwable;
        }
    }

    @Override
    public void removeTickingArea(TickingArea area) {
        TickingArea removed = areaMap.remove(area.getUuid());
        if (removed == null) return;
        storage.removeTickingArea(removed.getUuid());
        bumpVersion();
        releaseChunkView(removed);
    }

    @Override
    public void removeTickingArea(String name) {
        TickingArea area = getTickingArea(name);
        if (area != null) removeTickingArea(area);
    }

    @Override
    public void removeAllTickingArea() {
        Set<TickingArea> areas = new HashSet<>(areaMap.values());
        storage.removeAllTickingArea();
        areaMap.clear();
        bumpVersion();
        areas.forEach(SimpleTickingAreaManager::releaseChunkView);
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
        areas.forEach(SimpleTickingAreaManager::releaseChunkView);
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
        return getTickingAreas(level).size();
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
        bumpVersion();
    }

    @Override
    public void loadAllTickingArea() {
        Set<TickingArea> previous = new HashSet<>(areaMap.values());
        areaMap.clear();
        previous.forEach(SimpleTickingAreaManager::releaseChunkView);

        Map<UUID, TickingArea> loaded = storage.readTickingArea();

        try {
            for (TickingArea area : loaded.values()) {
                Level level = getAreaLevel(area, true);
                level.updateTickingAreaChunkView(area);
            }

            areaMap.putAll(loaded);
            bumpVersion();

            for (TickingArea area : areaMap.values()) {
                if (area.isPreload()) {
                    Preconditions.checkState(area.loadAllChunk(), "Failed to preload ticking area chunks: %s", area.getName());
                }
            }

            for (TickingArea area : areaMap.values()) {
                if (!area.isPreload()) {
                    Preconditions.checkState(area.loadAllChunk(), "Failed to load ticking area chunks: %s", area.getName());
                }
            }
        } catch (RuntimeException | Error throwable) {
            areaMap.clear();
            bumpVersion();
            loaded.values().forEach(SimpleTickingAreaManager::releaseChunkView);
            throw throwable;
        }
    }

    private static Level getAreaLevel(TickingArea area, boolean load) {
        Level level = Server.getInstance().getLevelByName(area.getLevelName());

        if (level == null && load && Server.getInstance().loadLevel(area.getLevelName())) {
            level = Server.getInstance().getLevelByName(area.getLevelName());
        }

        Preconditions.checkState(level != null, "Ticking area level is not loaded: %s", area.getLevelName());
        Preconditions.checkState(
                level.getDimensionData().getDimensionId() == area.getDimensionId(),
                "Ticking area dimension does not match level %s", area.getLevelName());
        return level;
    }

    private static void releaseChunkView(TickingArea area) {
        Level level = Server.getInstance().getLevelByName(area.getLevelName());
        if (level == null || level.getDimensionData().getDimensionId() != area.getDimensionId()) return;
        level.removeTickingAreaChunkView(area.getUuid());
    }
}
