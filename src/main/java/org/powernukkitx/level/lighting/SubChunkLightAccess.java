package org.powernukkitx.level.lighting;

/**
 * Defines the light and block access required by subchunk relighting.
 *
 * @author Curse
 */
public interface SubChunkLightAccess {

    /**
     * Returns whether the light cell is available.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @return the requested value
     */
    boolean isAvailable(int x, int y, int z);

    /**
     * Returns the light filter.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @return the requested value
     */
    int getLightFilter(int x, int y, int z);

    /**
     * Returns the removal light filter.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @return the requested value
     */
    default int getRemovalLightFilter(int x, int y, int z) {
        return getLightFilter(x, y, z);
    }

    /**
     * Returns block light emission.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @return the requested value
     */
    int getBlockEmission(int x, int y, int z);

    /**
     * Returns block light at the position.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @return the requested value
     */
    int getBlockLight(int x, int y, int z);

    /**
     * Sets block light at the position.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @param light value for this API
     */
    void setBlockLight(int x, int y, int z, int light);

    /**
     * Returns sky light at the position.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @return the requested value
     */
    int getSkyLight(int x, int y, int z);

    /**
     * Sets sky light at the position.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @param light value for this API
     */
    void setSkyLight(int x, int y, int z, int light);
}
