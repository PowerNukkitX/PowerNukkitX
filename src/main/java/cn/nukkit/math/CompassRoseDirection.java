package cn.nukkit.math;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a 16 direction compass rose.
 * <p>https://en.wikipedia.org/wiki/Compass_rose#/media/File:Brosen_windrose.svg
 */
public enum CompassRoseDirection {
    NORTH(0, -1, BlockFace.NORTH, 0, 8),
    EAST(1, 0, BlockFace.EAST, 90, 12),
    SOUTH(0, 1, BlockFace.SOUTH, 180, 0),
    WEST(-1, 0, BlockFace.WEST, 270, 4),
    NORTH_EAST(NORTH, EAST, BlockFace.NORTH, 45, 10),
    NORTH_WEST(NORTH, WEST, BlockFace.WEST, 315, 6),
    SOUTH_EAST(SOUTH, EAST, BlockFace.EAST, 135, 14),
    SOUTH_WEST(SOUTH, WEST, BlockFace.SOUTH, 225, 2),
    WEST_NORTH_WEST(WEST, NORTH_WEST, BlockFace.WEST, 292.5, 5),
    NORTH_NORTH_WEST(NORTH, NORTH_WEST, BlockFace.NORTH, 337.5, 7),
    NORTH_NORTH_EAST(NORTH, NORTH_EAST, BlockFace.NORTH, 22.5, 9),
    EAST_NORTH_EAST(EAST, NORTH_EAST, BlockFace.EAST, 67.5, 11),
    EAST_SOUTH_EAST(EAST, SOUTH_EAST, BlockFace.EAST, 112.5, 13),
    SOUTH_SOUTH_EAST(SOUTH, SOUTH_EAST, BlockFace.SOUTH, 157.5, 15),
    SOUTH_SOUTH_WEST(SOUTH, SOUTH_WEST, BlockFace.SOUTH, 202.5, 1),
    WEST_SOUTH_WEST(WEST, SOUTH_WEST, BlockFace.WEST, 247.5, 3);

    private final static CompassRoseDirection[] VALUES = new CompassRoseDirection[]{
            SOUTH, SOUTH_SOUTH_WEST, SOUTH_WEST, WEST_SOUTH_WEST,
            WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST,
            NORTH, NORTH_NORTH_EAST, NORTH_EAST, EAST_NORTH_EAST,
            EAST, EAST_SOUTH_EAST, SOUTH_EAST, SOUTH_SOUTH_EAST
    };

    private final int modX;
    private final int modZ;
    private final BlockFace closestBlockFace;
    private final float yaw;
    private final int index;

    CompassRoseDirection(int modX, int modZ, BlockFace closestBlockFace, double yaw, int index) {
        this.modX = modX;
        this.modZ = modZ;
        this.closestBlockFace = closestBlockFace;
        this.yaw = (float) yaw;
        this.index = index;
    }

    CompassRoseDirection(CompassRoseDirection face1, CompassRoseDirection face2, BlockFace closestBlockFace, double yaw, int index) {
        this.modX = face1.getModX() + face2.getModX();
        this.modZ = face1.getModZ() + face2.getModZ();
        this.closestBlockFace = closestBlockFace;
        this.yaw = (float) yaw;
        this.index = index;
    }

    /**
     * Get the amount of X-coordinates to modify to get the represented block
     *
     * @return Amount of X-coordinates to modify
     */
    public int getModX() {
        return modX;
    }

    /**
     * Get the amount of Z-coordinates to modify to get the represented block
     *
     * @return Amount of Z-coordinates to modify
     */
    public int getModZ() {
        return modZ;
    }

    /**
     * Gets the closest face for this direction. For example, NNE returns N.
     * Even directions like NE will return the direction to the left, N in this case.
     */
    public BlockFace getClosestBlockFace() {
        return closestBlockFace;
    }

    public int getIndex() {
        return index;
    }

    /**
     * Gets the closes direction based on the given {@link cn.nukkit.entity.Entity} yaw.
     *
     * @param yaw An entity yaw
     * @return The closest direction
     * @since 1.4.0.0-PN
     */
    public static CompassRoseDirection getClosestFromYaw(double yaw, @NotNull Precision precision) {
        return CompassRoseDirection.from((int) Math.round(Math.round((yaw + 180.0) * precision.directions / 360.0) * (16.0 / precision.directions)) & 0x0f);
    }

    /**
     * Gets the closes direction based on the given {@link cn.nukkit.entity.Entity} yaw.
     *
     * @param yaw An entity yaw
     * @return The closest direction
     * @since 1.4.0.0-PN
     */
    public static CompassRoseDirection getClosestFromYaw(double yaw) {
        return getClosestFromYaw(yaw, Precision.SECONDARY_INTER_CARDINAL);
    }

    public CompassRoseDirection getOppositeFace() {
        return switch (this) {
            case NORTH -> CompassRoseDirection.SOUTH;
            case SOUTH -> CompassRoseDirection.NORTH;
            case EAST -> CompassRoseDirection.WEST;
            case WEST -> CompassRoseDirection.EAST;
            case NORTH_EAST -> CompassRoseDirection.SOUTH_WEST;
            case NORTH_WEST -> CompassRoseDirection.SOUTH_EAST;
            case SOUTH_EAST -> CompassRoseDirection.NORTH_WEST;
            case SOUTH_WEST -> CompassRoseDirection.NORTH_EAST;
            case WEST_NORTH_WEST -> CompassRoseDirection.EAST_SOUTH_EAST;
            case NORTH_NORTH_WEST -> CompassRoseDirection.SOUTH_SOUTH_EAST;
            case NORTH_NORTH_EAST -> CompassRoseDirection.SOUTH_SOUTH_WEST;
            case EAST_NORTH_EAST -> CompassRoseDirection.WEST_SOUTH_WEST;
            case EAST_SOUTH_EAST -> CompassRoseDirection.WEST_NORTH_WEST;
            case SOUTH_SOUTH_EAST -> CompassRoseDirection.NORTH_NORTH_WEST;
            case SOUTH_SOUTH_WEST -> CompassRoseDirection.NORTH_NORTH_EAST;
            case WEST_SOUTH_WEST -> CompassRoseDirection.EAST_NORTH_EAST;
            default -> throw new IncompatibleClassChangeError("New values was added to the enum");
        };
    }

    /**
     * Gets the {@link cn.nukkit.entity.Entity} yaw that represents this direction.
     *
     * @return The yaw value that can be used by entities to look at this direction.
     * @since 1.4.0.0-PN
     */
    public float getYaw() {
        return yaw;
    }

    public static CompassRoseDirection from(int index) {
        return VALUES[index];
    }

    @RequiredArgsConstructor
    public enum Precision {
        /**
         * North, South, East, West.
         */
        CARDINAL(4),

        /**
         * N, E, S, W, NE, NW, SE, SW.
         */
        PRIMARY_INTER_CARDINAL(8),

        /**
         * N, E, S, W, NE, NW, SE, SW, WNW, NNW, NNE, ENE, ESE, SSE, SSW, WSW.
         */
        SECONDARY_INTER_CARDINAL(16);
        final int directions;
    }
}
