package org.powernukkitx.event.vehicle;

import org.powernukkitx.entity.item.EntityVehicle;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;


/**
 * Is called when an entity takes damage
 */
public class VehicleDamageEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private double damage;

    /**
     * Constructor for the VehicleDamageEvent
     *
     * @param vehicle the damaged vehicle
     * @param damage  the caused damage on the vehicle
     */
    public VehicleDamageEvent(final EntityVehicle vehicle, final double damage) {
        super(vehicle);

        this.damage = damage;
    }

    /**
     * Returns the caused damage on the vehicle
     *
     * @return caused damage on the vehicle
     */

    public double getDamage() {
        return damage;
    }

    /**
     * Sets the damage caused on the vehicle
     *
     * @param damage the caused damage
     */

    public void setDamage(final double damage) {
        this.damage = damage;
    }
}
