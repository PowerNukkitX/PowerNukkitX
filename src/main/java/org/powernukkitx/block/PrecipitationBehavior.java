package org.powernukkitx.block;

/**
 * Defines how a block interacts with precipitation, precipitation behavior component.
 *
 * @author Curse
 */
public enum PrecipitationBehavior {
    NONE(0),
    OBSTRUCT_RAIN(1),
    OBSTRUCT_RAIN_ACCUMULATE_SNOW(2),
    SNOWLOGGING(3);

    private final byte id;

    PrecipitationBehavior(int id) {
        this.id = (byte) id;
    }

    byte id() {
        return id;
    }

    static PrecipitationBehavior fromId(byte id) {
        return switch (id) {
            case 0 -> NONE;
            case 1 -> OBSTRUCT_RAIN;
            case 2 -> OBSTRUCT_RAIN_ACCUMULATE_SNOW;
            case 3 -> SNOWLOGGING;
            default -> throw new IllegalArgumentException("Unknown precipitation behavior: " + id);
        };
    }

    /**
     * Returns whether this behavior obstructs rain.
     */
    public boolean obstructsRain() {
        return this == OBSTRUCT_RAIN || this == OBSTRUCT_RAIN_ACCUMULATE_SNOW;
    }

    /**
     * Returns whether this behavior allows accumulated snow.
     */
    public boolean accumulatesSnow() {
        return this == OBSTRUCT_RAIN_ACCUMULATE_SNOW;
    }

    /**
     * Returns whether this behavior allows snowlogging.
     */
    public boolean isSnowLoggable() {
        return this == SNOWLOGGING;
    }
}
