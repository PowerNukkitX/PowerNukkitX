package org.powernukkitx.level.lighting;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkSection;

/**
 * Runs initial lighting work for a chunk until direct light and propagation can be completed.
 *
 * @author Curse
 */
public final class InitialLightingTask {
    private final Chunk centerChunk;
    private final int minSectionY;
    private final int maxSectionY;
    /**
     * Creates a new InitialLightingTask instance.
     *
     * @param centerChunk value for this API
     */
    public InitialLightingTask(Chunk centerChunk) {
        this.centerChunk = centerChunk;
        this.minSectionY = centerChunk.getDimensionData().getMinSectionY();
        this.maxSectionY = centerChunk.getDimensionData().getMaxSectionY();
    }

    /**
     * Processes queued work.
     * @return the requested value
     */
    public boolean process() {
        InitialSubChunkLighter lighter = new InitialSubChunkLighter(centerChunk, true);
        boolean[] processed = null;
        for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
            ChunkSection section = centerChunk.getSection(sectionY);

            if (section == null || !section.needsInitLighting()) continue;
            if (!lighter.processSection(sectionY)) return false;

            section.setNeedsInitLighting(false);
            if (processed == null) processed = new boolean[maxSectionY - minSectionY + 1];
            processed[sectionY - minSectionY] = true;
        }

        if (processed == null || !lighter.skippedNeighborStartedLighting()) return true;

        InitialSubChunkLighter full = new InitialSubChunkLighter(centerChunk, false);
        for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
            if (processed[sectionY - minSectionY] && !full.processSection(sectionY)) return false;
        }

        return true;
    }
}
