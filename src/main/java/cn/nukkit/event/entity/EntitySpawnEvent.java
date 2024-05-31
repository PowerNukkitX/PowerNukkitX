package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class EntitySpawnEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int entityType;
    /**
     * @deprecated 
     */
    

    public EntitySpawnEvent(cn.nukkit.entity.Entity entity) {
        this.entity = entity;
        this.entityType = entity.getNetworkId();
    }

    public Position getPosition() {
        return this.entity.getPosition();
    }
    /**
     * @deprecated 
     */
    

    public int getType() {
        return this.entityType;
    }
    /**
     * @deprecated 
     */
    

    public boolean isCreature() {
        return this.entity instanceof EntityCreature;
    }
    /**
     * @deprecated 
     */
    

    public boolean isHuman() {
        return this.entity instanceof EntityHuman;
    }
    /**
     * @deprecated 
     */
    

    public boolean isProjectile() {
        return this.entity instanceof EntityProjectile;
    }
    /**
     * @deprecated 
     */
    

    public boolean isVehicle() {
        return this.entity instanceof Entity;
    }
    /**
     * @deprecated 
     */
    

    public boolean isItem() {
        return this.entity instanceof EntityItem;
    }

}
