package cn.nukkit.level.tickingarea.storage;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.tickingarea.TickingArea;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface TickingAreaStorage {
    void addTickingArea(TickingArea area);

    default void addTickingArea(@NotNull TickingArea... areas) {
        for (var area : areas) {
            addTickingArea(area);
        }
    }

    Map<String, TickingArea> readTickingArea();

    void removeTickingArea(String name);

    void removeAllTickingArea();

    boolean containTickingArea(String name);
}
