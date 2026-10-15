package org.powernukkitx.level.lighting;

/**
 * Packs subchunk-relative light coordinates into compact integer work indexes.
 *
 * @author Curse
 */
public final class SubChunkLightIndex {
    /*
     * SubChunkLightIndex layout:
     *
     *     bits  0..3   local Y
     *     bits  4..5   SubChunk Y
     *
     *     bits  6..9   local Z
     *     bits 10..11  SubChunk Z
     *
     *     bits 12..15  local X
     *     bits 16..17  SubChunk X
     */
    public static final int NEGATIVE_Y = -0x00001;
    /**
     * Packed offset for the subchunk above.
     */
    public static final int POSITIVE_Y = 0x00001;
    /**
     * Packed offset for the north subchunk.
     */
    public static final int NEGATIVE_Z = -0x00040;
    /**
     * Packed offset for the south subchunk.
     */
    public static final int POSITIVE_Z = 0x00040;
    /**
     * Packed offset for the west subchunk.
     */
    public static final int NEGATIVE_X = -0x01000;
    /**
     * Packed offset for the east subchunk.
     */
    public static final int POSITIVE_X = 0x01000;
    private SubChunkLightIndex() {}

    /**
     * Packs coordinates into a light index.
     *
     * @param subChunkX value for this API
     * @param subChunkY value for this API
     * @param subChunkZ value for this API
     * @param localX value for this API
     * @param localY value for this API
     * @param localZ value for this API
     * @return the requested value
     */
    public static int pack(int subChunkX, int subChunkY, int subChunkZ, int localX, int localY, int localZ) {
        return (localY & 0x0f)
                | (subChunkY & 0x03) << 4
                | (localZ & 0x0f) << 6
                | (subChunkZ & 0x03) << 10
                | (localX & 0x0f) << 12
                | (subChunkX & 0x03) << 16;
    }

    /**
     * Returns the subchunk X offset.
     *
     * @param index value for this API
     * @return the requested value
     */
    public static int subChunkX(int index) {
        return index >>> 16 & 0x03;
    }

    /**
     * Returns the subchunk Y offset.
     *
     * @param index value for this API
     * @return the requested value
     */
    public static int subChunkY(int index) {
        return index >>> 4 & 0x03;
    }

    /**
     * Returns the subchunk Z offset.
     *
     * @param index value for this API
     * @return the requested value
     */
    public static int subChunkZ(int index) {
        return index >>> 10 & 0x03;
    }

    /**
     * Returns the local X coordinate.
     *
     * @param index value for this API
     * @return the requested value
     */
    public static int localX(int index) {
        return index >>> 12 & 0x0f;
    }

    /**
     * Returns the local Y coordinate.
     *
     * @param index value for this API
     * @return the requested value
     */
    public static int localY(int index) {
        return index & 0x0f;
    }

    /**
     * Returns the local Z coordinate.
     *
     * @param index value for this API
     * @return the requested value
     */
    public static int localZ(int index) {
        return index >>> 6 & 0x0f;
    }

    /**
     * Returns whether a packed index belongs to the active computation neighborhood.
     */
    public static boolean isInterior(int index) {
        return index >= 0
                && index < 0x30000
                && (index & 0x00030) != 0x00030
                && (index & 0x00c00) != 0x00c00;
    }

    /**
     * Returns the local cell index.
     *
     * @param index value for this API
     * @return the requested value
     */
    public static int cellIndex(int index) {
        return (index >>> 4 & 0x0f00) | (index >>> 2 & 0x00f0) | (index & 0x000f);
    }

    /**
     * Returns the packed index used by the lighting work bitsets.
     */
    public static int workIndex(int index) {
        return index;
    }
}
