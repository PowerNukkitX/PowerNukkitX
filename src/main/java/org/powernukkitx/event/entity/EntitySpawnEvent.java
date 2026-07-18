package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCreature;
import org.powernukkitx.entity.EntityHuman;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.Position;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class EntitySpawnEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int entityType;

    public EntitySpawnEvent(org.powernukkitx.entity.Entity entity) {
        this.entity = entity;
        this.entityType = entity.getNetworkId();
    }

    public Position getPosition() {
        return this.entity.getPosition();
    }

    public int getType() {
        return this.entityType;
    }

    public boolean isCreature() {
        return this.entity instanceof EntityCreature;
    }

    public boolean isHuman() {
        return this.entity instanceof EntityHuman;
    }

    public boolean isProjectile() {
        return this.entity instanceof EntityProjectile;
    }

    public boolean isVehicle() {
        return this.entity instanceof Entity;
    }

    public boolean isItem() {
        return this.entity instanceof EntityItem;
    }

}
