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
        InitialSubChunkLighter lighter = new InitialSubChunkLighter(centerChunk);
        for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
            ChunkSection section = centerChunk.getSection(sectionY);

            if (section == null || !section.needsInitLighting()) continue;
            if (!lighter.processSection(sectionY)) return false;

            section.setNeedsInitLighting(false);
        }

        return true;
    }
}
