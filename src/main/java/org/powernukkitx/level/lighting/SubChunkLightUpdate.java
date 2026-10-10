package org.powernukkitx.level.lighting;

/**
 * Describes a block or sky light update inside one subchunk.
 *
 * @param localX value for this API
 * @param localY value for this API
 * @param localZ value for this API
 * @param oldLight value for this API
 * @param newLight value for this API
 * @param oldFilter value for this API
 * @param newFilter value for this API
 * @param type value for this API
 *
 * @author Curse
 */
public record SubChunkLightUpdate(int localX, int localY, int localZ, int oldLight, int newLight, int oldFilter, int newFilter, Type type) {
    public SubChunkLightUpdate {
        if (localX < 0 || localX > 15) {
            throw new IllegalArgumentException("localX must be between 0 and 15");
        }

        if (localY < 0 || localY > 15) {
            throw new IllegalArgumentException("localY must be between 0 and 15");
        }

        if (localZ < 0 || localZ > 15) {
            throw new IllegalArgumentException("localZ must be between 0 and 15");
        }

        if (oldLight < 0 || oldLight > 15) {
            throw new IllegalArgumentException("oldLight must be between 0 and 15");
        }

        if (newLight < 0 || newLight > 15) {
            throw new IllegalArgumentException("newLight must be between 0 and 15");
        }

        if (oldFilter < 0 || oldFilter > 255) {
            throw new IllegalArgumentException("oldFilter must be between 0 and 255");
        }

        if (newFilter < 0 || newFilter > 255) {
            throw new IllegalArgumentException("newFilter must be between 0 and 255");
        }

        if (type == null) {
            throw new NullPointerException("type");
        }
    }

    /**
     * Creates a block light update.
     *
     * @param localX value for this API
     * @param localY value for this API
     * @param localZ value for this API
     * @param oldLight value for this API
     * @param newLight value for this API
     * @param oldFilter value for this API
     * @param newFilter value for this API
     * @return the requested value
     */
    public static SubChunkLightUpdate block(int localX, int localY, int localZ, int oldLight, int newLight, int oldFilter, int newFilter) {
        return new SubChunkLightUpdate(localX, localY, localZ, oldLight, newLight, oldFilter, newFilter, Type.BLOCK);
    }

    /**
     * Creates a sky light update.
     *
     * @param localX value for this API
     * @param localY value for this API
     * @param localZ value for this API
     * @param oldLight value for this API
     * @param newLight value for this API
     * @param oldFilter value for this API
     * @param newFilter value for this API
     * @return the requested value
     */
    public static SubChunkLightUpdate sky(int localX, int localY, int localZ, int oldLight, int newLight, int oldFilter, int newFilter) {
        return new SubChunkLightUpdate(localX, localY, localZ, oldLight, newLight, oldFilter, newFilter, Type.SKY);
    }

    /**
     * Returns whether light changed.
     * @return the requested value
     */
    public boolean lightChanged() {
        return oldLight != newLight;
    }

    /**
     * Returns whether filter changed.
     * @return the requested value
     */
    public boolean filterChanged() {
        return oldFilter != newFilter;
    }

    /**
     * Identifies the light channel affected by a subchunk light update.
     *
     * @author Curse
     */
    public enum Type {
        BLOCK(0),
        SKY(1);
        private final int id;
        Type(int id) {
            this.id = id;
        }

        /**
         * Returns the type ID.
         * @return the requested value
         */
        public int getId() {
            return id;
        }
    }
}
