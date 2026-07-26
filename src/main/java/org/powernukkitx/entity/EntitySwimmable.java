package org.powernukkitx.entity;

/**
 * A mob that implements this interface can swim.
 */


public interface EntitySwimmable {
    /**
     * @return whether this entity takes drowning damage
     */
    default boolean canDrown() {
        return false;
    }
}
