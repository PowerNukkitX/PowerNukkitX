package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.collection.nb.Long2ObjectNonBlockingMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockUpdateScheduler {
    private final Level level;
    private long lastTick;
    private final Long2ObjectNonBlockingMap<Set<BlockUpdateEntry>> queuedUpdates;

    private Set<BlockUpdateEntry> pendingUpdates;

    public BlockUpdateScheduler(Level level, long currentTick) {
        queuedUpdates = new Long2ObjectNonBlockingMap<>(); // Change to ConcurrentHashMap if this needs to be concurrent
        lastTick = currentTick;
        this.level = level;
    }

    public void tick(long currentTick) {
        // Should only perform once, unless ticks were skipped
        if (currentTick - lastTick < Short.MAX_VALUE) {// Arbitrary
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
            if (updates != null) {
                Iterator<BlockUpdateEntry> updateIterator = updates.iterator();

                while (updateIterator.hasNext()) {
                    BlockUpdateEntry entry = updateIterator.next();

                    Vector3 pos = entry.pos;
                    if (level.isChunkLoaded(NukkitMath.floorDouble(pos.x) >> 4, NukkitMath.floorDouble(pos.z) >> 4)) {
                        Block block = level.getBlock(entry.pos, entry.block.layer);

                        updateIterator.remove();
                        if (Block.equals(block, entry.block, false) && entry.checkBlockWhenUpdate) {
                            block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
                        } else {
                            block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
                        }
                    } else {
                        level.scheduleUpdate(entry.block, entry.pos, 0);
                    }
                }
            }
        } finally {
            pendingUpdates = null;
        }
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        Set<BlockUpdateEntry> set = new HashSet<>();

        for (var tickSet : this.queuedUpdates.values()) {
            for (BlockUpdateEntry update : tickSet) {
                Vector3 pos = update.pos;

                if (pos.getX() >= boundingBox.getMinX() && pos.getX() < boundingBox.getMaxX() &&
                        pos.getZ() >= boundingBox.getMinZ() && pos.getZ() < boundingBox.getMaxZ()) {
                    set.add(update);
                }
            }
        }

        return set;
    }

    public boolean isBlockTickPending(Vector3 pos, Block block) {
        Set<BlockUpdateEntry> tmpUpdates = pendingUpdates;
        if (tmpUpdates == null || tmpUpdates.isEmpty()) return false;
        return tmpUpdates.contains(new BlockUpdateEntry(pos, block));
    }

    private long getMinTime(BlockUpdateEntry entry) {
        return Math.max(entry.delay, lastTick + 1);
    }

    public void add(BlockUpdateEntry entry) {
        long time = getMinTime(entry);
        Set<BlockUpdateEntry> updateSet = queuedUpdates.get(time);
        if (updateSet == null) {
            Set<BlockUpdateEntry> tmp = queuedUpdates.putIfAbsent(time, updateSet = ConcurrentHashMap.newKeySet());
            if (tmp != null) updateSet = tmp;
        }
        updateSet.add(entry);
    }

    public boolean contains(BlockUpdateEntry entry) {
        for (var tickUpdateSet : queuedUpdates.values()) {
            if (tickUpdateSet.contains(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(BlockUpdateEntry entry) {
        for (var tickUpdateSet : queuedUpdates.values()) {
            if (tickUpdateSet.remove(entry)) {
                return true;
            }
        }
        return false;
    }
}
