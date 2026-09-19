package org.powernukkitx.level.lighting;
import lombok.extern.slf4j.Slf4j;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkFinalizationState;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.ArrayDeque;

/**
 * Schedules initial lighting work while preventing overlapping relight neighborhoods.
 *
 * @author Curse
 */
@Slf4j
public final class InitialLightingManager {
    private final Level level;
    private static final int RELIGHT_NEIGHBORHOOD_RADIUS = 1;
    private static final int MAX_PARALLEL_RELIGHTS = 2;
    private final ArrayDeque<Chunk> pendingChunks = new ArrayDeque<>();
    private final LongOpenHashSet reservedNeighborhoodChunks = new LongOpenHashSet();
    private final int maxConcurrentTasks;
    private int activeTaskCount;

    /**
     * Creates an initial lighting manager for a level.
     */
    public InitialLightingManager(Level level) {
        this.level = level;
        final int parallelism = level.getServer().getComputeThreadPool().getParallelism();
        this.maxConcurrentTasks = Math.max(1, Math.min(MAX_PARALLEL_RELIGHTS, parallelism - 1));
    }

    /**
     * Returns the managed level.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Returns whether chunks are waiting for initial lighting.
     */
    public synchronized boolean hasPendingWork() {
        return !pendingChunks.isEmpty();
    }

    /**
     * Returns whether an initial lighting task is active.
     */
    public synchronized boolean isTaskActive() {
        return activeTaskCount > 0;
    }

    /**
     * Returns the number of chunks waiting for initial lighting.
     */
    public synchronized int getPendingCount() {
        return pendingChunks.size();
    }

    /**
     * Schedules a finalized chunk for initial lighting.
     */
    public boolean schedule(Chunk chunk) {
        if (chunk == null || chunk.getFinalizationState() != ChunkFinalizationState.DONE) return false;
        if (!chunk.compareAndSetLightingState(ChunkLightingState.NEEDS_LIGHTING, ChunkLightingState.LIGHTING)) return false;

        synchronized (this) {
            pendingChunks.offer(chunk);
            scheduleAvailable();
        }

        return true;
    }

    /**
     * Schedules a chunk when it still requires initial lighting.
     */
    public boolean scheduleIfNeeded(Chunk chunk) {
        if (chunk == null || !chunk.needsLighting()) return false;
        return schedule(chunk);
    }

    private void scheduleAvailable() {
        while (activeTaskCount < maxConcurrentTasks) {
            Chunk chunk = pollNextRunnableChunk();
            if (chunk == null) {
                return;
            }

            reserveNeighborhood(chunk);
            activeTaskCount++;
            level.getServer()
                    .getComputeThreadPool()
                    .execute(
                            () -> {
                                try {
                                    runChunkRelight(chunk);
                                } finally {
                                    finishTask(chunk);
                                }
                            });
        }
    }

    private Chunk pollNextRunnableChunk() {
        int scanCount = pendingChunks.size();
        while (scanCount-- > 0) {
            Chunk chunk = pendingChunks.poll();
            if (chunk == null) {
                return null;
            }

            if (canReserveNeighborhood(chunk)) {
                return chunk;
            }

            pendingChunks.offer(chunk);
        }

        return null;
    }

    private boolean canReserveNeighborhood(Chunk chunk) {
        for (int offsetX = -RELIGHT_NEIGHBORHOOD_RADIUS;
                offsetX <= RELIGHT_NEIGHBORHOOD_RADIUS;
                offsetX++) {
            for (int offsetZ = -RELIGHT_NEIGHBORHOOD_RADIUS;
                    offsetZ <= RELIGHT_NEIGHBORHOOD_RADIUS;
                    offsetZ++) {
                long chunkHash = Level.chunkHash(chunk.getX() + offsetX, chunk.getZ() + offsetZ);
                if (reservedNeighborhoodChunks.contains(chunkHash)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void reserveNeighborhood(Chunk chunk) {
        for (int offsetX = -RELIGHT_NEIGHBORHOOD_RADIUS;
                offsetX <= RELIGHT_NEIGHBORHOOD_RADIUS;
                offsetX++) {
            for (int offsetZ = -RELIGHT_NEIGHBORHOOD_RADIUS;
                    offsetZ <= RELIGHT_NEIGHBORHOOD_RADIUS;
                    offsetZ++) {
                reservedNeighborhoodChunks.add(
                        Level.chunkHash(chunk.getX() + offsetX, chunk.getZ() + offsetZ));
            }
        }
    }

    private void releaseNeighborhood(Chunk chunk) {
        for (int offsetX = -RELIGHT_NEIGHBORHOOD_RADIUS;
                offsetX <= RELIGHT_NEIGHBORHOOD_RADIUS;
                offsetX++) {
            for (int offsetZ = -RELIGHT_NEIGHBORHOOD_RADIUS;
                    offsetZ <= RELIGHT_NEIGHBORHOOD_RADIUS;
                    offsetZ++) {
                reservedNeighborhoodChunks.remove(
                        Level.chunkHash(chunk.getX() + offsetX, chunk.getZ() + offsetZ));
            }
        }
    }

    private void finishTask(Chunk chunk) {
        synchronized (this) {
            releaseNeighborhood(chunk);
            activeTaskCount--;
            scheduleAvailable();
        }
    }

    private void runChunkRelight(Chunk chunk) {
        boolean success = false;
        try {
            DirectSkyInitializer.prepare(chunk);
            InitialLightingTask task = new InitialLightingTask(chunk);
            success = task.process();
        } catch (Throwable throwable) {
            log.error(
                    "Failed to run initial lighting for chunk ({}, {})",
                    chunk.getX(),
                    chunk.getZ(),
                    throwable);
        }

        if (success) {
            if (!chunk.compareAndSetLightingState(
                    ChunkLightingState.LIGHTING, ChunkLightingState.LIGHTING_FINISHED)) {
                log.debug(
                        "Chunk ({}, {}) changed lighting state while Chunk Relight was running",
                        chunk.getX(),
                        chunk.getZ());
                return;
            }

            completeLighting(chunk);
            return;
        }

        chunk.compareAndSetLightingState(
                ChunkLightingState.LIGHTING, ChunkLightingState.NEEDS_LIGHTING);
    }

    /**
     * Completes a successfully processed initial lighting task.
     */
    public boolean completeLighting(Chunk chunk) {
        if (chunk == null) {
            return false;
        }

        if (!chunk.compareAndSetLightingState(
                ChunkLightingState.LIGHTING_FINISHED, ChunkLightingState.LOADED)) {
            return false;
        }

        return true;
    }
}
