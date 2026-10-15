package org.powernukkitx.scheduler;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerationState;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.BlockUpdateEntry;

import java.util.List;
import java.util.Map;

/**
 * Schedules random block updates and dispatches due entries in tick order.
 *
 * @author Curse
 */
public class RandomBlockUpdateScheduler {
    private static final int MAX_UPDATES_PER_TICK = 100;
    private static final int LIVE_AVAILABILITY_RADIUS = 8;

    private final IChunk chunk;
    private final ScheduledBlockUpdateQueue updates = new ScheduledBlockUpdateQueue();
    private volatile long lastTick;

    /**
     * Creates a new RandomBlockUpdateScheduler instance.
     *
     * @param chunk value for this API
     * @param currentTick value for this API
     */
    public RandomBlockUpdateScheduler(IChunk chunk, long currentTick) {
        this.lastTick = currentTick;
        this.chunk = chunk;
    }

    /**
     * Advances this object by one server tick.
     *
     * @param currentTick value for this API
     */
    public void tick(long currentTick) {
        lastTick = currentTick;

        if (updates.isEmpty()) {
            return;
        }

        List<ScheduledBlockUpdate> batch = updates.beginDue(currentTick, MAX_UPDATES_PER_TICK);
        try {
            for (ScheduledBlockUpdate update : batch) {
                perform(update);
            }
        } finally {
            updates.finishCurrent();
        }
    }

    private void perform(ScheduledBlockUpdate update) {
        chunk.setChanged(true);

        Level level = chunk.getLevel();

        if (!isLiveAreaAvailable(level, update.x, update.z, LIVE_AVAILABILITY_RADIUS)) {
            if (isLiveChunkAvailable(level, update.x >> 4, update.z >> 4)) {
                updates.requeue(update, lastTick);
            }
            return;
        }

        Block block = level.getBlock(update.x, update.y, update.z, update.layer);
        if (!update.blockState.getIdentifier().equals(block.getId())) {
            return;
        }
        if (!block.isTickingDisabled()) {
            block.onUpdate(Level.BLOCK_UPDATE_RANDOM);
        }
    }

    private static boolean isLiveAreaAvailable(Level level, int x, int z, int radius) {
        int minChunkX = (x - radius) >> 4;
        int maxChunkX = (x + radius) >> 4;
        int minChunkZ = (z - radius) >> 4;
        int maxChunkZ = (z + radius) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (!isLiveChunkAvailable(level, chunkX, chunkZ)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isLiveChunkAvailable(Level level, int chunkX, int chunkZ) {
        IChunk target = level.getPhysicalChunkIfLoaded(chunkX, chunkZ);
        return target != null && target.getGenerationState() == ChunkGenerationState.COMPLETE;
    }

    /**
     * Adds one persisted random update.
     */
    public void add(Block block, Vector3 pos, long targetTick, int order, boolean checkBlockWhenUpdate) {
        long time = Math.max(targetTick, lastTick + 1);
        updates.add(ScheduledBlockUpdate.from(block, pos, time, order, checkBlockWhenUpdate));
        chunk.setChanged(true);
    }

    /**
     * Adds a value.
     *
     * @param entry value for this API
     */
    public void add(BlockUpdateEntry entry) {
        long time = Math.max(entry.delay, lastTick + 1);
        updates.add(ScheduledBlockUpdate.from(entry, time));
        chunk.setChanged(true);
    }

    /**
     * Restores one persisted random update without marking the queue dirty.
     */
    public void addLoaded(
            Block block,
            int x,
            int y,
            int z,
            long targetTick,
            int order,
            boolean checkBlockWhenUpdate
    ) {
        updates.addLoaded(new ScheduledBlockUpdate(
                x, y, z, block.layer, order, targetTick, block.getBlockState(), checkBlockWhenUpdate
        ));
    }

    /**
     * Returns whether a matching pending update exists.
     */
    public boolean contains(Vector3 pos, Block block) {
        return updates.contains(pos, block);
    }

    /**
     * Returns whether the value is present.
     *
     * @param entry value for this API
     * @return the requested value
     */
    public boolean contains(BlockUpdateEntry entry) {
        return contains(entry.pos, entry.block);
    }

    /**
     * Removes one matching pending update.
     */
    public boolean remove(Vector3 pos, Block block) {
        boolean removed = updates.remove(pos, block);
        if (removed) {
            chunk.setChanged(true);
        }
        return removed;
    }

    /**
     * Removes a registered value.
     *
     * @param entry value for this API
     * @return the requested value
     */
    public boolean remove(BlockUpdateEntry entry) {
        return remove(entry.pos, entry.block);
    }

    /**
     * Returns whether a block tick is pending.
     *
     * @param pos value for this API
     * @param block value for this API
     * @return the requested value
     */
    public boolean isBlockTickPending(Vector3 pos, Block block) {
        return updates.isCurrent(pos, block);
    }

    /**
     * Returns the last processed tick.
     *
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
     *
     * @return the requested value
     */
    public Map<BlockUpdateEntry, Long> getPendingBlockUpdatesWithTime() {
        return updates.snapshotWithTime(chunk.getLevel());
    }

    /**
     * Visits persisted pending/deferred records directly.
     */
    public int forEachPending(ScheduledBlockUpdateVisitor visitor) {
        return updates.forEachPending(visitor);
    }

    /**
     * Returns whether this queue component changed since successful persistence.
     */
    public boolean hasStorageChanges() {
        return updates.hasStorageChanges();
    }

    /**
     * Returns the current queue persistence change version.
     */
    public long getStorageChangeVersion() {
        return updates.getStorageChangeVersion();
    }

    /**
     * Marks one successfully persisted queue version.
     */
    public void markStorageSaved(long version) {
        updates.markStorageSaved(version);
    }
}
