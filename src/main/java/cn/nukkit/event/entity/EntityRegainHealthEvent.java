package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityRegainHealthEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;
    public static final int $5 = 3;

    private float amount;
    private final int reason;
    /**
     * @deprecated 
     */
    

    public EntityRegainHealthEvent(Entity entity, float amount, int regainReason) {
        this.entity = entity;
        this.amount = amount;
        this.reason = regainReason;
    }
    /**
     * @deprecated 
     */
    

    public float getAmount() {
        return amount;
    }
    /**
     * @deprecated 
     */
    

    public void setAmount(float amount) {
        this.amount = amount;
    }
    /**
     * @deprecated 
     */
    

    public int getRegainReason() {
        return reason;
    }
}
