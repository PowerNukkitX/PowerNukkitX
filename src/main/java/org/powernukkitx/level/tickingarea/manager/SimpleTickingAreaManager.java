package org.powernukkitx.level.tickingarea.manager;

import org.powernukkitx.level.Position;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.powernukkitx.level.tickingarea.storage.TickingAreaStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleTickingAreaManager extends TickingAreaManager {

    protected Map<String, TickingArea> areaMap;

    public SimpleTickingAreaManager(TickingAreaStorage storage) {
        super(storage);
        areaMap = storage.readTickingArea();
    }

    @Override
    public void addTickingArea(TickingArea area) {
        areaMap.put(area.getName(), area);
        storage.addTickingArea(area);
        bumpVersion();
    }

    @Override
    public void removeTickingArea(String name) {
        areaMap.remove(name);
        storage.removeTickingArea(name);
        bumpVersion();
    }

    @Override
    public void removeAllTickingArea() {
        areaMap.clear();
        storage.removeAllTickingArea();
        bumpVersion();
    }

    @Override
    public @Nullable TickingArea getTickingArea(String name) {
        return areaMap.get(name);
    }

    @Override
    public boolean containTickingArea(String name) {
        return areaMap.containsKey(name);
    }

    @Override
    public Set<TickingArea> getAllTickingArea() {
        return new HashSet<>(areaMap.values());
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
    public void loadAllTickingArea() {
        for (TickingArea area : new ArrayList<>(areaMap.values()))
            if (!area.loadAllChunk()) removeTickingArea(area.getName());
    }
}
