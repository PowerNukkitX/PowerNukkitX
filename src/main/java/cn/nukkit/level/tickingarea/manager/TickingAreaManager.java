package cn.nukkit.level.tickingarea.manager;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Position;
import cn.nukkit.level.tickingarea.TickingArea;
import cn.nukkit.level.tickingarea.storage.TickingAreaStorage;

import java.util.Set;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class TickingAreaManager {

    protected TickingAreaStorage storage;

    public TickingAreaManager(TickingAreaStorage storage) {
        this.storage = storage;
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
