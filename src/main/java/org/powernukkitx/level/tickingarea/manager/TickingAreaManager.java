package org.powernukkitx.level.tickingarea.manager;

import org.powernukkitx.level.Position;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.powernukkitx.level.tickingarea.storage.TickingAreaStorage;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;


public abstract class TickingAreaManager {

    protected TickingAreaStorage storage;
    private final AtomicLong version = new AtomicLong();

    public TickingAreaManager(TickingAreaStorage storage) {
        this.storage = storage;
    }

    /**
     * Monotonic counter bumped on every area addition/removal. Lets consumers cache
     * derived data (e.g. per-level chunk lists) and cheaply detect staleness.
     */
    public long getVersion() {
        return version.get();
    }

    protected void bumpVersion() {
        version.incrementAndGet();
    }

    public abstract void addTickingArea(TickingArea area);

    public abstract void removeTickingArea(String name);

    public abstract void removeAllTickingArea();

    public abstract TickingArea getTickingArea(String name);

    public abstract boolean containTickingArea(String name);

    public abstract Set<TickingArea> getAllTickingArea();

    public abstract TickingArea getTickingAreaByChunk(String levelName, TickingArea.ChunkPos chunkPos);

    public abstract TickingArea getTickingAreaByPos(Position pos);

    public abstract void loadAllTickingArea();

    public TickingAreaStorage getStorage() {
        return storage;
    }
}
