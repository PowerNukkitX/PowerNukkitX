package org.powernukkitx.level.lighting;

import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkSection;

/**
 * Initializes direct sky light values for chunk columns before normal propagation runs.
 *
 * @author Curse
 */
public final class DirectSkyInitializer {
    private static final int MAX_LIGHT = 15;
    private DirectSkyInitializer() {}

    /**
     * Prepares the chunk.
     *
     * @param chunk value for this API
     */
    public static void prepare(Chunk chunk) {
        if (chunk == null || chunk.isLightingReady()) return;

        int minHeight = chunk.getDimensionData().getMinHeight();
        int maxHeight = chunk.getDimensionData().getMaxHeight();
        int minSectionY = chunk.getDimensionData().getMinSectionY();
        int maxSectionY = chunk.getDimensionData().getMaxSectionY();

        if (!chunk.isOverWorld()) {
            for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
                ChunkSection section = chunk.getSection(sectionY);
                if (section == null) {
                    continue;
                }
                section.setAllSkyLight((byte) 0);
            }
            return;
        }

        int[] skyStarts = new int[16 * 16];
        int minimumSkyStart = Integer.MAX_VALUE;
        int maximumSkyStart = Integer.MIN_VALUE;
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int skyStart = chunk.getHeightMap(x, z);
                skyStarts[(z << 4) | x] = skyStart;
                if (skyStart < minimumSkyStart) {
                    minimumSkyStart = skyStart;
                }

                if (skyStart > maximumSkyStart) {
                    maximumSkyStart = skyStart;
                }
            }
        }

        for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
            int sectionMinY = Math.max(sectionY << 4, minHeight);
            int sectionMaxY = Math.min((sectionY << 4) + 15, maxHeight);
            ChunkSection section = chunk.getSection(sectionY);

            if (sectionMaxY < minimumSkyStart) {
                if (section != null) section.setAllSkyLight((byte) 0);
                continue;
            }

            if (sectionMinY >= maximumSkyStart) {
                if (section != null) section.setAllSkyLight((byte) MAX_LIGHT);
                continue;
            }

            if (section == null) section = chunk.getOrCreateSectionForLighting(sectionY);
            if (section == null) continue;

            section.setAllSkyLight((byte) 0);
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int skyStart = skyStarts[(z << 4) | x];
                    int firstY = Math.max(sectionMinY, skyStart);
                    for (int y = firstY; y <= sectionMaxY; y++) {
                        section.setBlockSkyLight(x, y & 0x0f, z, (byte) MAX_LIGHT);
                    }
                }
            }
        }
    }
}
