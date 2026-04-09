package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Dimension;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityLevelChangeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Dimension originLevel;
    private final Dimension targetLevel;

    public EntityLevelChangeEvent(Entity entity, Dimension originLevel, Dimension targetLevel) {
        this.entity = entity;
        this.originLevel = originLevel;
        this.targetLevel = targetLevel;
    }

    public Dimension getOrigin() {
        return originLevel;
    }

    public Dimension getTarget() {
        return targetLevel;
    }
}
