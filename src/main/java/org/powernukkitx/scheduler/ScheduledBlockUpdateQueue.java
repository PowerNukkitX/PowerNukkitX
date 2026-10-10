package org.powernukkitx.scheduler;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.BlockUpdateEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Stores scheduled block updates in pending/current/deferred containers.
 *
 * @author Curse
 */
final class ScheduledBlockUpdateQueue {
    private final PriorityQueue<ScheduledBlockUpdate> pending = new PriorityQueue<>();
    private final ArrayList<ScheduledBlockUpdate> current = new ArrayList<>();
    private final PriorityQueue<ScheduledBlockUpdate> deferred = new PriorityQueue<>();
    private long storageChanges;
    private long persistedStorageChanges;

    synchronized void add(ScheduledBlockUpdate update) {
        pending.add(update);
        storageChanges++;
    }

    synchronized void addLoaded(ScheduledBlockUpdate update) {
        pending.add(update);
    }

    synchronized void requeue(ScheduledBlockUpdate update, long targetTick) {
        pending.add(new ScheduledBlockUpdate(
                update.x,
                update.y,
                update.z,
                update.layer,
                update.order,
                targetTick,
                update.blockState,
                update.checkBlockWhenUpdate
        ));
        storageChanges++;
    }

    synchronized boolean isEmpty() {
        return pending.size() == 0 && deferred.size() == 0;
    }

    synchronized List<ScheduledBlockUpdate> beginDue(long currentTick, int limit) {
        if (current.size() != 0) {
            throw new IllegalStateException("Scheduled block update batch is already active");
        }

        while (current.size() < limit && pending.size() != 0) {
            ScheduledBlockUpdate update = pending.peek();
            if (Long.compareUnsigned(update.targetTick, currentTick) > 0) {
                break;
            }

            current.add(pending.poll());
        }

        if (current.size() != 0) {
            storageChanges++;
        }

        return current;
    }

    synchronized void finishCurrent() {
        current.clear();
    }

    synchronized boolean contains(Vector3 pos, Block block) {
        return contains(pending, pos, block) || contains(deferred, pos, block);
    }

    synchronized boolean remove(Vector3 pos, Block block) {
        boolean removed = remove(pending, pos, block) || remove(deferred, pos, block);
        if (removed) {
            storageChanges++;
        }
        return removed;
    }

    synchronized boolean isCurrent(Vector3 pos, Block block) {
        for (ScheduledBlockUpdate update : current) {
            if (update.matches(pos, block)) {
                return true;
            }
        }

        return false;
    }

    synchronized boolean isConcurrentSchedule(Vector3 pos, Block block, long targetTick, int delay) {
        return isConcurrentSchedule(pending, pos, block, targetTick, delay)
                || isConcurrentSchedule(deferred, pos, block, targetTick, delay);
    }

    synchronized Set<BlockUpdateEntry> snapshot(Level level) {
        Set<BlockUpdateEntry> result = new HashSet<>();
        addSnapshot(result, pending, level);
        addSnapshot(result, deferred, level);
        return result;
    }

    synchronized Set<BlockUpdateEntry> snapshot(AxisAlignedBB boundingBox, Level level) {
        Set<BlockUpdateEntry> result = new HashSet<>();
        addSnapshot(result, pending, boundingBox, level);
        addSnapshot(result, deferred, boundingBox, level);
        return result;
    }

    synchronized Map<BlockUpdateEntry, Long> snapshotWithTime(Level level) {
        Map<BlockUpdateEntry, Long> result = new IdentityHashMap<>(pending.size() + deferred.size());
        addSnapshot(result, pending, level);
        addSnapshot(result, deferred, level);
        return result;
    }

    synchronized int forEachPending(ScheduledBlockUpdateVisitor visitor) {
        int count = 0;

        for (ScheduledBlockUpdate update : pending) {
            visitor.accept(update.x, update.y, update.z, update.blockState, update.targetTick);
            count++;
        }

        for (ScheduledBlockUpdate update : deferred) {
            visitor.accept(update.x, update.y, update.z, update.blockState, update.targetTick);
            count++;
        }

        return count;
    }

    synchronized boolean hasStorageChanges() {
        return storageChanges != persistedStorageChanges;
    }

    synchronized long getStorageChangeVersion() {
        return storageChanges;
    }

    synchronized void markStorageSaved(long version) {
        persistedStorageChanges = Math.max(persistedStorageChanges, version);
    }

    private static boolean contains(
            Iterable<ScheduledBlockUpdate> updates,
            Vector3 pos,
            Block block
    ) {
        for (ScheduledBlockUpdate update : updates) {
            if (update.matches(pos, block)) {
                return true;
            }
        }

        return false;
    }

    private static boolean remove(
            PriorityQueue<ScheduledBlockUpdate> updates,
            Vector3 pos,
            Block block
    ) {
        Iterator<ScheduledBlockUpdate> iterator = updates.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().matches(pos, block)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    private static boolean isConcurrentSchedule(
            Iterable<ScheduledBlockUpdate> updates,
            Vector3 pos,
            Block block,
            long targetTick,
            int delay
    ) {
        for (ScheduledBlockUpdate update : updates) {
            if (update.matches(pos, block) && targetTick <= update.targetTick + delay) {
                return true;
            }
        }

        return false;
    }

    private static void addSnapshot(
            Set<BlockUpdateEntry> result,
            Iterable<ScheduledBlockUpdate> updates,
            Level level
    ) {
        for (ScheduledBlockUpdate update : updates) {
            result.add(update.toLegacyEntry(level));
        }
    }

    private static void addSnapshot(
            Set<BlockUpdateEntry> result,
            Iterable<ScheduledBlockUpdate> updates,
            AxisAlignedBB boundingBox,
            Level level
    ) {
        for (ScheduledBlockUpdate update : updates) {
            if (update.x >= boundingBox.getMinX() && update.x < boundingBox.getMaxX()
                    && update.z >= boundingBox.getMinZ() && update.z < boundingBox.getMaxZ()) {
                result.add(update.toLegacyEntry(level));
            }
        }
    }

    private static void addSnapshot(
            Map<BlockUpdateEntry, Long> result,
            Iterable<ScheduledBlockUpdate> updates,
            Level level
    ) {
        for (ScheduledBlockUpdate update : updates) {
            result.put(update.toLegacyEntry(level), update.targetTick);
        }
    }
}
