package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.CommonBlockProperties;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a 16 direction compass rose.
 * <p>https://en.wikipedia.org/wiki/Compass_rose#/media/File:Brosen_windrose.svg
 */


public enum CompassRoseDirection {


    private final int modX;
    private final int modZ;
    private final BlockFace closestBlockFace;
    private final float yaw;

    CompassRoseDirection(int modX, int modZ, BlockFace closestBlockFace, double yaw) {
        this.modX = modX;
        this.modZ = modZ;
        this.closestBlockFace = closestBlockFace;
        this.yaw = (float) yaw;
    }

    CompassRoseDirection(CompassRoseDirection face1, CompassRoseDirection face2, BlockFace closestBlockFace, double yaw) {
        this.modX = face1.getModX() + face2.getModX();
        this.modZ = face1.getModZ() + face2.getModZ();
        this.closestBlockFace = closestBlockFace;
        this.yaw = (float) yaw;
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

    /**
     * Gets the closes direction based on the given {@link cn.nukkit.entity.Entity} yaw.
     *
     * @param yaw An entity yaw
     * @return The closest direction
     * @since 1.4.0.0-PN
     */


    public static CompassRoseDirection getClosestFromYaw(double yaw, @NotNull Precision precision) {
        return CommonBlockProperties.GROUND_SIGN_DIRECTION.getValueForMeta(
                (int) Math.round(Math.round((yaw + 180.0) * precision.directions / 360.0) * (16.0 / precision.directions)) & 0x0f
        );
    }

    /**
     * Gets the closes direction based on the given {@link cn.nukkit.entity.Entity} yaw.
     * @param yaw An entity yaw
     * @return The closest direction
     * @since 1.4.0.0-PN
     */


    public static CompassRoseDirection getClosestFromYaw(double yaw) {
        return getClosestFromYaw(yaw, Precision.SECONDARY_INTER_CARDINAL);
    }


    public CompassRoseDirection getOppositeFace() {
        switch (this) {
            case NORTH:
                return CompassRoseDirection.SOUTH;

            case SOUTH:
                return CompassRoseDirection.NORTH;

            case EAST:
                return CompassRoseDirection.WEST;

            case WEST:
                return CompassRoseDirection.EAST;

            case NORTH_EAST:
                return CompassRoseDirection.SOUTH_WEST;

            case NORTH_WEST:
                return CompassRoseDirection.SOUTH_EAST;

            case SOUTH_EAST:
                return CompassRoseDirection.NORTH_WEST;

            case SOUTH_WEST:
                return CompassRoseDirection.NORTH_EAST;

            case WEST_NORTH_WEST:
                return CompassRoseDirection.EAST_SOUTH_EAST;

            case NORTH_NORTH_WEST:
                return CompassRoseDirection.SOUTH_SOUTH_EAST;

            case NORTH_NORTH_EAST:
                return CompassRoseDirection.SOUTH_SOUTH_WEST;

            case EAST_NORTH_EAST:
                return CompassRoseDirection.WEST_SOUTH_WEST;

            case EAST_SOUTH_EAST:
                return CompassRoseDirection.WEST_NORTH_WEST;

            case SOUTH_SOUTH_EAST:
                return CompassRoseDirection.NORTH_NORTH_WEST;

            case SOUTH_SOUTH_WEST:
                return CompassRoseDirection.NORTH_NORTH_EAST;

            case WEST_SOUTH_WEST:
                return CompassRoseDirection.EAST_NORTH_EAST;
            
            default:
                throw new IncompatibleClassChangeError("New values was added to the enum");
        }
    }

    /**
     * Gets the {@link cn.nukkit.entity.Entity} yaw that represents this direction.
     * @return The yaw value that can be used by entities to look at this direction.
     * @since 1.4.0.0-PN
     */


    public float getYaw() {
        return yaw;
    }


    @RequiredArgsConstructor
    public enum Precision {
        /**
         * North, South, East, West.
         */


        /**
         * N, E, S, W, NE, NW, SE, SW.
         */


        /**
         * N, E, S, W, NE, NW, SE, SW, WNW, NNW, NNE, ENE, ESE, SSE, SSW, WSW.
         */

        protected final int directions;
    }
}
