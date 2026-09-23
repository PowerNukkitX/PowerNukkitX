package org.powernukkitx.level.tickingarea.manager;

import com.google.common.base.Preconditions;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.tickingarea.TickingArea;
import org.powernukkitx.level.tickingarea.storage.TickingAreaStorage;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;


public abstract class TickingAreaManager {
    public static final int DEFAULT_MAX_TICKING_AREAS = 10;

    protected TickingAreaStorage storage;
    private final AtomicLong version = new AtomicLong();

    public TickingAreaManager(TickingAreaStorage storage) {
        this.storage = storage;
    }

    /**
     * Monotonic counter bumped when ticking-area state changes.
     */
    public long getVersion() {
        return version.get();
    }

    protected void bumpVersion() {
        version.incrementAndGet();
    }

    /**
     * Returns the maximum number of ticking areas allowed for a level.
     *
     * @param level target level
     * @return maximum ticking-area count
     */
    public int getMaxTickingAreas(Level level) {
        return storage.getMaxTickingAreas(level, DEFAULT_MAX_TICKING_AREAS);
    }

    /**
     * Sets the maximum number of ticking areas allowed for a level.
     *
     * @param level target level
     * @param maxTickingAreas maximum ticking-area count
     */
    public void setMaxTickingAreas(Level level, int maxTickingAreas) {
        Preconditions.checkArgument(maxTickingAreas > 0, "Maximum ticking areas must be greater than zero");
        storage.setMaxTickingAreas(level, maxTickingAreas);
    }

    /**
     * Resets the level-specific ticking-area limit.
     *
     * @param level target level
     */
    public void resetMaxTickingAreas(Level level) {
        storage.resetMaxTickingAreas(level);
    }

    /**
     * Returns whether another ticking area may be added to the level.
     *
     * @param level target level
     * @return whether another area can be added
     */
    public boolean canAddTickingArea(Level level) {
        return getTickingAreaCount(level) < getMaxTickingAreas(level);
    }

    /**
     * Returns the number of ticking areas associated with a level.
     *
     * @param level target level
     * @return ticking-area count
     */
    public abstract int getTickingAreaCount(Level level);

    public abstract void addTickingArea(TickingArea area);

    /**
     * Removes the specified ticking area.
     *
     * @param area ticking area to remove
     */
    public abstract void removeTickingArea(TickingArea area);

    public abstract void removeTickingArea(String name);

    public abstract void removeAllTickingArea();

    /**
     * Removes all ticking areas associated with a level.
     *
     * @param level target level
     */
    public abstract void removeAllTickingArea(Level level);

    public abstract TickingArea getTickingArea(String name);

    /**
     * Returns a named ticking area associated with a level.
     *
     * @param level target level
     * @param name ticking-area name
     * @return matching ticking area, or {@code null} when absent
     */
    public abstract TickingArea getTickingArea(Level level, String name);

    public abstract boolean containTickingArea(String name);

    /**
     * Returns whether the level contains a ticking area with the specified name.
     *
     * @param level target level
     * @param name ticking-area name
     * @return whether the ticking area exists
     */
    public abstract boolean containTickingArea(Level level, String name);

    public abstract Set<TickingArea> getAllTickingArea();

    /**
     * Returns ticking areas applicable to the specified level.
     *
     * @param level target level
     * @return matching ticking areas
     */
    public abstract Set<TickingArea> getTickingAreas(Level level);

    /**
     * Returns all ticking areas stored for the level's world.
     *
     * @param level target level
     * @return world ticking areas
     */
    public abstract Set<TickingArea> getTickingAreasInWorld(Level level);

    public boolean hasAreas() {
        Set<TickingArea> areas = getAllTickingArea();
        return areas != null && !areas.isEmpty();
    }

    public abstract TickingArea getTickingAreaByChunk(String levelName, TickingArea.ChunkPos chunkPos);

    public abstract TickingArea getTickingAreaByPos(Position pos);

    /**
     * Returns all ticking areas containing the specified position.
     *
     * @param pos position to query
     * @return matching ticking areas
     */
    public abstract Set<TickingArea> getTickingAreasByPos(Position pos);

    /**
     * Sets the preload state of a ticking area.
     *
     * @param area ticking area
     * @param preload whether preloading is enabled
     */
    public abstract void setTickingAreaPreload(TickingArea area, boolean preload);

    public abstract void loadAllTickingArea();

    public TickingAreaStorage getStorage() {
        return storage;
    }
}
