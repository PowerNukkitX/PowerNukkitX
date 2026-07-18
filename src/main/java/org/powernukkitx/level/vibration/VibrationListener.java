package org.powernukkitx.level.vibration;


import org.powernukkitx.entity.Entity;
import org.powernukkitx.math.Vector3;

/**
 * A vibration listener
 */


public interface VibrationListener {

    /**
     * Returns the position of the vibration listener
     *
     * @return Vector3
     */
    Vector3 getListenerVector();

    /**
     * Whether to respond to this vibration
     * If it responds, a sound wave will be emitted from the sound source to the listener position, and onVibrationArrive() will be called when it arrives
     * Note that if this method is called, the sound wave is guaranteed to be able to arrive
     *
     * @param event the vibration event
     * @return boolean
     */
    boolean onVibrationOccur(VibrationEvent event);

    /**
     * Sound wave arrival event
     *
     * @param event the vibration event
     */
    void onVibrationArrive(VibrationEvent event);

    /**
     * Returns the vibration listening radius
     *
     * @return double
     */
    double getListenRange();

    /**
     * Whether it is an entity
     * If it is an entity, the entity-specific nbt tag will be used when sending sound wave particles
     * If not, this listener is treated as a block (eg: sculk sensor)
     *
     * @return boolean
     */
    default boolean isEntity() {
        return this instanceof Entity;
    }

    /**
     * Returns the entity object corresponding to this vibration listener, on the premise that isEntity() is true
     *
     * @return Entity
     */
    default Entity asEntity() {
        return (Entity) this;
    }
}
