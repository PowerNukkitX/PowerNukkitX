package org.powernukkitx.level.tickingarea.storage;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;


public interface TickingAreaStorage {
    void addTickingArea(TickingArea area);

    default void addTickingArea(@NotNull TickingArea... areas) {
        for (var area : areas) {
            addTickingArea(area);
        }
    }

    Map<UUID, TickingArea> readTickingArea();

    /**
     * Removes a persisted ticking area by UUID.
     *
     * @param uuid ticking-area UUID
     */
    void removeTickingArea(UUID uuid);

    void removeTickingArea(String name);

    void removeAllTickingArea();

    boolean containTickingArea(String name);

    /**
     * Returns the persisted ticking-area limit for a level.
     *
     * @param level target level
     * @param defaultValue value returned when no override is persisted
     * @return maximum ticking-area count
     */
    int getMaxTickingAreas(Level level, int defaultValue);

    /**
     * Persists the ticking-area limit for a level.
     *
     * @param level target level
     * @param maxTickingAreas maximum ticking-area count
     */
    void setMaxTickingAreas(Level level, int maxTickingAreas);

    /**
     * Removes the persisted ticking-area limit override for a level.
     *
     * @param level target level
     */
    void resetMaxTickingAreas(Level level);
}
