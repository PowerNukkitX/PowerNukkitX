package org.powernukkitx.entity;

/**
 * An entity that implements this interface can fly.
 */


public interface EntityFlyable {
    /**
     * @return whether it takes falling damage
     */
    default boolean hasFallingDamage() {
        return false;
    }
}
