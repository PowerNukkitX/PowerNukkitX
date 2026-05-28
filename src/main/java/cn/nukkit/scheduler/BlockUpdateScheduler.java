package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.collection.nb.Long2ObjectNonBlockingMap;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BlockUpdateScheduler {
    private final IChunk chunk;
    private long lastTick;
    private final Long2ObjectNonBlockingMap<Set<BlockUpdateEntry>> queuedUpdates;

    private Set<BlockUpdateEntry> pendingUpdates;

    public BlockUpdateScheduler(IChunk chunk, long currentTick) {
        queuedUpdates = new Long2ObjectNonBlockingMap<>(); // Change to ConcurrentHashMap if this needs to be concurrent
        lastTick = currentTick;
        this.chunk = chunk;
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
                    updateIterator.remove();
                    if(pos.getChunkX() == chunk.getX() && pos.getChunkZ() == chunk.getZ()) {
                        Level level = chunk.getLevel();
                        Block block = level.getBlock(entry.pos, entry.block.layer);

                        if (block.isTickingDisabled()) {
                            continue;
                        }
                        block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
                    } else log.warn("Scheduled block {} in chunk {}, {}", entry.block.getId(), chunk.getX(), chunk.getZ());
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

    public Set<BlockUpdateEntry> getPendingBlockUpdates() {
        Set<BlockUpdateEntry> set = new HashSet<>();
        for (var tickSet : this.queuedUpdates.values()) {
            set.addAll(tickSet);
        }
        return set;
    }

    public boolean isBlockTickPending(Vector3 pos, Block block) {
        Set<BlockUpdateEntry> tmpUpdates = pendingUpdates;
        if (tmpUpdates == null || tmpUpdates.isEmpty()) return false;
        return tmpUpdates.contains(new BlockUpdateEntry(pos, block));
    }

    public boolean isConcurrentSchedule(Vector3 pos, Block block, long targetTick, int delay) {
        for (Map.Entry<Long, Set<BlockUpdateEntry>> entry : queuedUpdates.entrySet()) {
            long scheduledTick = entry.getKey();
            if (entry.getValue().contains(new BlockUpdateEntry(pos, block)) && targetTick <= scheduledTick + delay) {
                return true;
            }
        }
        return false;
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
