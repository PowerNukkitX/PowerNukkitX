package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.math.Vector3;


public class EntityMoveByPistonEvent extends EntityMotionEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    public EntityMoveByPistonEvent(Entity entity, Vector3 pos) {
        super(entity, pos);
    }
}
