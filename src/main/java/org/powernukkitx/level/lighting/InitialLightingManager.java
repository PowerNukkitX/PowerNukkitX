package org.powernukkitx.level.lighting;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.Chunk;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

/**
 * Performs initial chunk lighting.
 *
 * @author Curse
 */
@Slf4j
public final class InitialLightingManager {
    private final Level level;

    /**
     * Creates an initial lighting manager.
     *
     * @param level managed level
     */
    public InitialLightingManager(Level level) {
        this.level = Preconditions.checkNotNull(level);
    }

    /**
     * Returns the managed level.
     *
     * @return managed level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Processes initial lighting for a chunk.
     *
     * @param chunk target chunk
     * @return whether lighting completed
     */
    public boolean process(Chunk chunk) {
        if (chunk == null) return false;
        if (chunk.isLightingReady()) return true;

        if (!chunk.compareAndSetLightingState(ChunkLightingState.NEEDS_LIGHTING, ChunkLightingState.LIGHTING)) {
            return false;
        }

        boolean success;
        try {
            DirectSkyInitializer.prepare(chunk);
            success = new InitialLightingTask(chunk).process();
        } catch (Throwable throwable) {
            log.error("Failed to run initial lighting for chunk ({}, {})", chunk.getX(), chunk.getZ(), throwable);
            success = false;
        }

        if (!success) {
            chunk.compareAndSetLightingState(ChunkLightingState.LIGHTING, ChunkLightingState.NEEDS_LIGHTING);
            return false;
        }

        if (!chunk.compareAndSetLightingState(ChunkLightingState.LIGHTING, ChunkLightingState.LIGHTING_FINISHED)) {
            return false;
        }

        return completeLighting(chunk);
    }

    /**
     * Completes initial lighting state.
     *
     * @param chunk target chunk
     * @return whether lighting became ready
     */
    public boolean completeLighting(Chunk chunk) {
        return chunk != null && chunk.compareAndSetLightingState(
                ChunkLightingState.LIGHTING_FINISHED, ChunkLightingState.LOADED);
    }
}
