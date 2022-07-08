package cn.nukkit.level.tickingarea.storage;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.tickingarea.TickingArea;

import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface TickingAreaStorage {
    void addTickingArea(TickingArea area);
    void addTickingArea(TickingArea... areas);
    Map<String,TickingArea> readTickingArea();
    void removeTickingArea(String name);
    void removeAllTickingArea();
    boolean containTickingArea(String name);
}
