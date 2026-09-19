package org.powernukkitx.scheduler;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.BlockUpdateEntry;
import org.powernukkitx.utils.collection.nb.Long2ObjectNonBlockingMap;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Schedules random block updates and dispatches due entries in tick order.
 *
 * @author Curse
 */
@Slf4j
public class RandomBlockUpdateScheduler {
    private final IChunk chunk;
    private long lastTick;
    private final Long2ObjectNonBlockingMap<Set<BlockUpdateEntry>> queuedUpdates;
    private final Map<BlockUpdateEntry, Long> entryToTick = new ConcurrentHashMap<>();

    private Set<BlockUpdateEntry> pendingUpdates;

    /**
     * Creates a new RandomBlockUpdateScheduler instance.
     *
     * @param chunk value for this API
     * @param currentTick value for this API
     */
    public RandomBlockUpdateScheduler(IChunk chunk, long currentTick) {
        this.queuedUpdates = new Long2ObjectNonBlockingMap<>();
        this.lastTick = currentTick;
        this.chunk = chunk;
    }

    /**
     * Advances this object by one server tick.
     *
     * @param currentTick value for this API
     */
    public void tick(long currentTick) {
        if (entryToTick.isEmpty()) {
            lastTick = currentTick;
            return;
        }

        if (currentTick - lastTick < Short.MAX_VALUE) {
            for (long tick = lastTick + 1; tick <= currentTick; tick++) {
                perform(tick);
            }
        } else {
            ArrayList<Long> times = new ArrayList<>(queuedUpdates.keySet());
            Collections.sort(times);

            for (long tick : times) {
                if (tick <= currentTick) {
                    perform(tick);
                } else {
                    break;
                }
            }
        }

        lastTick = currentTick;
    }

    private void perform(long tick) {
        try {
            lastTick = tick;
            Set<BlockUpdateEntry> updates = pendingUpdates = queuedUpdates.remove(tick);

            if (updates == null) return;

            Iterator<BlockUpdateEntry> updateIterator = updates.iterator();

            while (updateIterator.hasNext()) {
                BlockUpdateEntry entry = updateIterator.next();

                Vector3 pos = entry.pos;

                updateIterator.remove();
                entryToTick.remove(entry, tick);
                chunk.setChanged(true);

                if (pos.getChunkX() != chunk.getX() || pos.getChunkZ() != chunk.getZ()) {
                    log.warn("Random scheduled block {} is outside chunk {}, {}", entry.block.getId(), chunk.getX(), chunk.getZ());
                    continue;
                }

                Level level = chunk.getLevel();
                Block block = level.getBlock(entry.pos, entry.block.layer);
                if (block.isTickingDisabled()) continue;

                block.onUpdate(Level.BLOCK_UPDATE_RANDOM);
            }
        } finally {
            pendingUpdates = null;
        }
    }

    private long getMinTime(BlockUpdateEntry entry) {
        return Math.max(entry.delay, lastTick + 1);
    }

    /**
     * Adds a value.
     *
     * @param entry value for this API
     */
    public void add(BlockUpdateEntry entry) {
        long time = getMinTime(entry);
        Set<BlockUpdateEntry> updateSet = queuedUpdates.get(time);

        if (updateSet == null) {
            Set<BlockUpdateEntry> existing = queuedUpdates.putIfAbsent(time, updateSet = ConcurrentHashMap.newKeySet());
            if (existing != null) updateSet = existing;
        }

        updateSet.add(entry);
        entryToTick.put(entry, time);
        chunk.setChanged(true);
    }

    /**
     * Returns whether the value is present.
     *
     * @param entry value for this API
     * @return the requested value
     */
    public boolean contains(BlockUpdateEntry entry) {
        return entryToTick.containsKey(entry);
    }

    /**
     * Removes a registered value.
     *
     * @param entry value for this API
     * @return the requested value
     */
    public boolean remove(BlockUpdateEntry entry) {
        Long tick = entryToTick.remove(entry);
        if (tick == null) return false;

        Set<BlockUpdateEntry> tickUpdateSet = queuedUpdates.get((long) tick);
        boolean removed = tickUpdateSet != null && tickUpdateSet.remove(entry);
        if (removed) chunk.setChanged(true);

        return removed;
    }

    /**
     * Returns whether a block tick is pending.
     *
     * @param pos value for this API
     * @param block value for this API
     * @return the requested value
     */
    public boolean isBlockTickPending(Vector3 pos, Block block) {
        Set<BlockUpdateEntry> updates = pendingUpdates;
        if (updates == null || updates.isEmpty()) return false;

        return updates.contains(new BlockUpdateEntry(pos, block));
    }

    /**
     * Returns the last processed tick.
     * @return the requested value
     */
    public long getLastTick() {
        return this.lastTick;
    }

    /**
     * Sets the last processed tick.
     *
     * @param lastTick value for this API
     */
    public void setLastTick(long lastTick) {
        this.lastTick = lastTick;
    }

    /**
     * Returns pending block updates with due ticks.
     * @return the requested value
     */
    public Map<BlockUpdateEntry, Long> getPendingBlockUpdatesWithTime() {
        return new HashMap<>(this.entryToTick);
    }
}
