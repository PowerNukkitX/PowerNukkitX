package org.powernukkitx.level.lighting;
import org.powernukkitx.Player;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Queues and processes runtime block and sky light updates.
 *
 * @author Curse
 */
public final class RuntimeLightingManager {
    /**
     * Default processing budget for one asynchronous lighting slice.
     */
    public static final long DEFAULT_TIME_BUDGET_NANOS = 500_000L;
    private final Level level;
    private final Long2ObjectOpenHashMap<ChunkPendingLighting> pendingChunks = new Long2ObjectOpenHashMap<>();
    private final Object pendingLock = new Object();
    private final ReentrantLock processingLock = new ReentrantLock();
    private final AtomicBoolean taskActive = new AtomicBoolean();
    private long nextSequence;

    /**
     * Creates a runtime lighting manager for a level.
     */
    public RuntimeLightingManager(Level level) {
        this.level = level;
    }

    /**
     * Returns the managed level.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Queues one runtime lighting update.
     */
    public void queueUpdate(int chunkX, int sectionY, int chunkZ, SubChunkLightUpdate update) {
        if (update == null) return;

        int minSectionY = level.getDimensionData().getMinSectionY();
        int maxSectionY = level.getDimensionData().getMaxSectionY();
        if (sectionY < minSectionY || sectionY > maxSectionY) return;

        synchronized (pendingLock) {
            long chunkHash = Level.chunkHash(chunkX, chunkZ);
            ChunkPendingLighting chunkPending = pendingChunks.get(chunkHash);
            if (chunkPending == null) {
                chunkPending = new ChunkPendingLighting(chunkX, chunkZ);
                pendingChunks.put(chunkHash, chunkPending);
            }

            SubChunkPendingLighting subChunkPending = chunkPending.subChunks.get(sectionY);
            if (subChunkPending == null) {
                subChunkPending = new SubChunkPendingLighting(sectionY, nextSequence++);
                chunkPending.subChunks.put(sectionY, subChunkPending);
            }

            subChunkPending.updates.add(update);
        }

        scheduleIfNeeded();
    }

    /**
     * Queues runtime lighting updates for one section.
     */
    public void queueUpdates(int chunkX, int sectionY, int chunkZ, Collection<SubChunkLightUpdate> updates) {
        if (updates == null || updates.isEmpty()) return;

        int minSectionY = level.getDimensionData().getMinSectionY();
        int maxSectionY = level.getDimensionData().getMaxSectionY();
        if (sectionY < minSectionY || sectionY > maxSectionY) return;

        boolean added = false;
        synchronized (pendingLock) {
            long chunkHash = Level.chunkHash(chunkX, chunkZ);
            ChunkPendingLighting chunkPending = pendingChunks.get(chunkHash);
            if (chunkPending == null) {
                chunkPending = new ChunkPendingLighting(chunkX, chunkZ);
                pendingChunks.put(chunkHash, chunkPending);
            }

            SubChunkPendingLighting subChunkPending = chunkPending.subChunks.get(sectionY);
            if (subChunkPending == null) {
                subChunkPending = new SubChunkPendingLighting(sectionY, nextSequence++);
                chunkPending.subChunks.put(sectionY, subChunkPending);
            }

            for (SubChunkLightUpdate update : updates) {
                if (update == null) continue;

                subChunkPending.updates.add(update);
                added = true;
            }

            if (!added && subChunkPending.updates.isEmpty()) {
                chunkPending.subChunks.remove(sectionY);
                if (chunkPending.subChunks.isEmpty()) {
                    pendingChunks.remove(chunkHash);
                }
            }
        }

        if (added) scheduleIfNeeded();
    }

    /**
     * Queues runtime lighting updates grouped by section.
     */
    public void queueUpdates(int chunkX, int chunkZ, Int2ObjectMap<? extends Collection<SubChunkLightUpdate>> updatesBySection) {
        if (updatesBySection == null || updatesBySection.isEmpty()) return;

        int minSectionY = level.getDimensionData().getMinSectionY();
        int maxSectionY = level.getDimensionData().getMaxSectionY();
        boolean added = false;
        synchronized (pendingLock) {
            long chunkHash = Level.chunkHash(chunkX, chunkZ);
            ChunkPendingLighting chunkPending = pendingChunks.get(chunkHash);
            for (var entry : updatesBySection.int2ObjectEntrySet()) {
                int sectionY = entry.getIntKey();
                if (sectionY < minSectionY || sectionY > maxSectionY) continue;

                Collection<SubChunkLightUpdate> updates = entry.getValue();
                if (updates == null || updates.isEmpty()) continue;

                if (chunkPending == null) {
                    chunkPending = new ChunkPendingLighting(chunkX, chunkZ);
                    pendingChunks.put(chunkHash, chunkPending);
                }

                SubChunkPendingLighting subChunkPending = chunkPending.subChunks.get(sectionY);
                if (subChunkPending == null) {
                    subChunkPending = new SubChunkPendingLighting(sectionY, nextSequence++);
                    chunkPending.subChunks.put(sectionY, subChunkPending);
                }

                for (SubChunkLightUpdate update : updates) {
                    if (update == null) continue;

                    subChunkPending.updates.add(update);
                    added = true;
                }
            }

            if (chunkPending != null && chunkPending.subChunks.isEmpty()) {
                pendingChunks.remove(chunkHash);
            }
        }

        if (added) scheduleIfNeeded();
    }

    /**
     * Returns whether runtime lighting work remains queued.
     */
    public boolean hasPendingWork() {
        synchronized (pendingLock) {
            for (ChunkPendingLighting chunkPending : pendingChunks.values()) {
                for (SubChunkPendingLighting subChunkPending : chunkPending.subChunks.values()) {
                    if (!subChunkPending.updates.isEmpty()) return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns whether asynchronous runtime lighting processing is active.
     */
    public boolean isTaskActive() {
        return taskActive.get();
    }

    /**
     * Processes one runtime lighting slice using the default budget.
     *
     * @return whether work remains pending
     */
    public boolean processSlice() {
        return processSlice(DEFAULT_TIME_BUDGET_NANOS);
    }

    /**
     * Processes runtime lighting work until the supplied budget is exhausted.
     *
     * @return whether work remains pending
     */
    public boolean processSlice(long timeBudgetNanos) {
        if (timeBudgetNanos <= 0) return hasPendingWork();

        processingLock.lock();
        try {
            long start = System.nanoTime();
            List<WorkItem> workItems = getListOfChunksWithPlayerDistance();
            boolean processedAny = false;
            for (WorkItem workItem : workItems) {
                // Always allow the slice to make progress before enforcing the budget.
                if (processedAny && System.nanoTime() - start >= timeBudgetNanos) {
                    break;
                }

                List<SubChunkLightUpdate> updates = drainUpdates(workItem);
                if (updates.isEmpty()) {
                    continue;
                }

                processWorkItem(workItem, updates);
                removeProcessedSubChunk(workItem);
                processedAny = true;
            }

            return hasPendingWork();
        } finally {
            processingLock.unlock();
        }
    }

    /**
     * Processes all queued runtime lighting work synchronously.
     */
    public void flush() {
        processingLock.lock();
        try {
            while (true) {
                List<WorkItem> workItems = getListOfChunksWithPlayerDistance();
                if (workItems.isEmpty()) return;

                boolean processedAny = false;
                for (WorkItem workItem : workItems) {
                    List<SubChunkLightUpdate> updates = drainUpdates(workItem);
                    if (updates.isEmpty()) continue;

                    processWorkItem(workItem, updates);
                    removeProcessedSubChunk(workItem);
                    processedAny = true;
                }

                if (!processedAny) return;
            }
        } finally {
            processingLock.unlock();
        }
    }

    private List<WorkItem> getListOfChunksWithPlayerDistance() {
        PlayerPosition[] players = snapshotPlayerPositions();
        List<WorkItem> workItems = new ArrayList<>();
        synchronized (pendingLock) {
            for (ChunkPendingLighting chunkPending : pendingChunks.values()) {
                for (SubChunkPendingLighting subChunkPending : chunkPending.subChunks.values()) {
                    if (subChunkPending.updates.isEmpty()) continue;

                    double playerDistance = getNearestPlayerDistanceSquared(chunkPending.chunkX, subChunkPending.sectionY, chunkPending.chunkZ, players);
                    workItems.add(new WorkItem(chunkPending.chunkX, subChunkPending.sectionY, chunkPending.chunkZ, subChunkPending.sequence, playerDistance));
                }
            }
        }

        workItems.sort(Comparator.comparingDouble(WorkItem::playerDistanceSquared).thenComparingLong(WorkItem::sequence));
        return workItems;
    }

    private PlayerPosition[] snapshotPlayerPositions() {
        Collection<Player> players = level.getPlayers().values();
        if (players.isEmpty()) return new PlayerPosition[0];

        PlayerPosition[] positions = new PlayerPosition[players.size()];
        int index = 0;
        for (Player player : players) {
            positions[index++] = new PlayerPosition(player.getX(), player.getY(), player.getZ());
        }

        if (index == positions.length) return positions;

        PlayerPosition[] resized = new PlayerPosition[index];
        System.arraycopy(positions, 0, resized, 0, index);
        return resized;
    }

    private static double getNearestPlayerDistanceSquared(int chunkX, int sectionY, int chunkZ, PlayerPosition[] players) {
        if (players.length == 0) return Double.POSITIVE_INFINITY;

        double centerX = (chunkX << 4) + 8.0;
        double centerY = (sectionY << 4) + 8.0;
        double centerZ = (chunkZ << 4) + 8.0;
        double nearest = Double.POSITIVE_INFINITY;
        for (PlayerPosition player : players) {
            double deltaX = player.x - centerX;
            double deltaY = player.y - centerY;
            double deltaZ = player.z - centerZ;
            double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (distanceSquared < nearest) {
                nearest = distanceSquared;
            }
        }

        return nearest;
    }

    private List<SubChunkLightUpdate> drainUpdates(WorkItem workItem) {
        synchronized (pendingLock) {
            long chunkHash = Level.chunkHash(workItem.chunkX, workItem.chunkZ);
            ChunkPendingLighting chunkPending = pendingChunks.get(chunkHash);
            if (chunkPending == null) {
                return List.of();
            }

            SubChunkPendingLighting subChunkPending = chunkPending.subChunks.get(workItem.sectionY);
            if (subChunkPending == null || subChunkPending.updates.isEmpty()) {
                return List.of();
            }

            List<SubChunkLightUpdate> result = new ArrayList<>(subChunkPending.updates);
            // Keep the queue entry until processing completes so concurrent additions are preserved.
            subChunkPending.updates.clear();
            return result;
        }
    }

    private void processWorkItem(WorkItem workItem, List<SubChunkLightUpdate> updates) {
        IChunk loadedChunk = level.getProvider().getLoadedChunk(workItem.chunkX, workItem.chunkZ);
        if (!(loadedChunk instanceof Chunk chunk)) return;

        ServerSubChunkLighter lighter = new ServerSubChunkLighter(chunk);
        lighter.queueUpdates(workItem.sectionY, updates);
        lighter.process();
    }

    private void removeProcessedSubChunk(WorkItem workItem) {
        synchronized (pendingLock) {
            long chunkHash = Level.chunkHash(workItem.chunkX, workItem.chunkZ);
            ChunkPendingLighting chunkPending = pendingChunks.get(chunkHash);
            if (chunkPending == null) return;

            SubChunkPendingLighting subChunkPending = chunkPending.subChunks.get(workItem.sectionY);
            if (subChunkPending != null && subChunkPending.updates.isEmpty()) {
                chunkPending.subChunks.remove(workItem.sectionY);
            }

            if (chunkPending.subChunks.isEmpty()) {
                pendingChunks.remove(chunkHash);
            }
        }
    }

    private void scheduleIfNeeded() {
        if (!hasPendingWork()) return;
        if (!taskActive.compareAndSet(false, true)) return;
        level.getServer().getComputeThreadPool().execute(this::runRuntimeRelighterTask);
    }

    private void runRuntimeRelighterTask() {
        try {
            processSlice(DEFAULT_TIME_BUDGET_NANOS);
        } finally {
            taskActive.set(false);
            if (hasPendingWork()) {
                scheduleIfNeeded();
            }
        }
    }

    private static final class ChunkPendingLighting {
        private final int chunkX;
        private final int chunkZ;
        private final Int2ObjectOpenHashMap<SubChunkPendingLighting> subChunks = new Int2ObjectOpenHashMap<>();
        private ChunkPendingLighting(int chunkX, int chunkZ) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }
    }

    private static final class SubChunkPendingLighting {
        private final int sectionY;
        private final long sequence;
        private final List<SubChunkLightUpdate> updates = new ArrayList<>();
        private SubChunkPendingLighting(int sectionY, long sequence) {
            this.sectionY = sectionY;
            this.sequence = sequence;
        }
    }

    private record WorkItem(int chunkX, int sectionY, int chunkZ, long sequence, double playerDistanceSquared) {
    }

    private static final class PlayerPosition {
        private final double x;
        private final double y;
        private final double z;
        private PlayerPosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
